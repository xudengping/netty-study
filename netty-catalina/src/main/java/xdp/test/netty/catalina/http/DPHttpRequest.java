package xdp.test.netty.catalina.http;

import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class DPHttpRequest {
	
	private ChannelHandlerContext context;
	private HttpRequest request;
	
	public DPHttpRequest(ChannelHandlerContext context, HttpRequest request) {
		this.context = context;
		this.request = request;
	}
	
	public String getUri(){
		return request.getUri();
	}
	
	public String getMethod(){
		return request.getMethod().name();
	}
	
	public Map<String, List<String>> getParameters(){
		QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
		return decoderQuery.parameters();
	}
	
	public String getParameter(String name){
		QueryStringDecoder decoderQuery = new QueryStringDecoder(request.getUri());
		Map<String, List<String>> parameters = decoderQuery.parameters();
		List<String> list = parameters.get(name);
		if(list != null){
			return list.get(0);
		}
		
		return null;
	}

}
