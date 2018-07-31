package xdp.test.netty.chat.handler;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest>{
	
	private static Logger LOG = Logger.getLogger(HttpHandler.class);
	
	// class的根目录
	private URL basrURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
	private static final String WEBROOT = "webroot";
	
	
	// 实现子类本身的方法
	@SuppressWarnings("deprecation")
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String uri = request.getUri();
		String page = uri.equals("/")?"chat.html":uri;	
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(getResourceFile(page), "r");
		}catch(Exception e) {
			ctx.fireChannelRead(request.retain());
			return;
		}
		
		
		HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
		String contextType = "text/html;";
		if(uri.endsWith(".css")) {
			contextType = "text/css;";
		}else if(uri.endsWith(".js")) {
			contextType = "text/javascript;";
		}else if(uri.toLowerCase().matches("(jpg|png|gif|ico)$")) {
			String ext = uri.substring(uri.lastIndexOf("."));
			contextType = "image/"+ext+";";
		}
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE,contextType+"charset=utf-8;");
		
		// 处理长连接
		boolean keepAlive = HttpHeaders.isKeepAlive(request);
		if(keepAlive) {
			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,file.length());
			response.headers().set(HttpHeaders.Names.CONNECTION,HttpHeaders.Values.KEEP_ALIVE);
		}
		
		ctx.write(response);
		ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
		// 清空缓冲区
		ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		if(!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
		
		file.close();
	}

	private File getResourceFile(String filename) throws Exception {
		String path = basrURL.toURI()+WEBROOT+"/"+filename;
		path = path.contains("file:/")?path.substring(5):path;
		path.replace("//", "/");
		return new File(path);
	}

}
