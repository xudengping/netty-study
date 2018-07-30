package xdp.test.netty.catalina.http;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class DPHttpResponse {
	
	private ChannelHandlerContext context;
	private HttpRequest request;
	
	public DPHttpResponse(ChannelHandlerContext context, HttpRequest request) {
		this.context = context;
		this.request = request;
	}
	
	public void write(String msg){
		if(msg == null){return;}
		try {
			FullHttpResponse response = new DefaultFullHttpResponse
					(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
			response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/json");
			response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,response.content().readableBytes());
			response.headers().set(HttpHeaders.Names.EXPIRES,0);
			if(HttpHeaders.isKeepAlive(request)){
				response.headers().set(HttpHeaders.Names.CONNECTION,Values.KEEP_ALIVE);
			}
			context.write(response);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			context.flush();
		}
		
	}
	
	

}
