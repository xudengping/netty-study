package xdp.test.netty.catalina.server;

import org.apache.log4j.Logger;

import xdp.test.netty.catalina.http.DPHttpRequest;
import xdp.test.netty.catalina.http.DPHttpResponse;
import xdp.test.netty.catalina.http.DPServlet;
import xdp.test.netty.catalina.servlet.MyDPServlet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;

public class DPTomcatHandler extends ChannelInboundHandlerAdapter{
	
	private static Logger LOG = Logger.getLogger(DPTomcatHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof HttpRequest){
			HttpRequest httpRequest = (HttpRequest) msg;
			DPHttpRequest request = new DPHttpRequest(ctx,httpRequest);
			DPHttpResponse response = new DPHttpResponse(ctx,httpRequest);
			
			DPServlet servlet = new MyDPServlet();
			servlet.doGet(request, response);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		LOG.info("DPTomcatHandler error:");
		cause.printStackTrace();
		ctx.close();
	}
	
	
	

}
