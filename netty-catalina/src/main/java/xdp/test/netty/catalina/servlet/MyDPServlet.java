package xdp.test.netty.catalina.servlet;

import xdp.test.netty.catalina.http.DPHttpRequest;
import xdp.test.netty.catalina.http.DPHttpResponse;
import xdp.test.netty.catalina.http.DPServlet;

public class MyDPServlet extends  DPServlet{

	@Override
	public void doGet(DPHttpRequest request, DPHttpResponse response) {
		response.write(request.getParameter("name"));
	}

	@Override
	public void doPost(DPHttpRequest request, DPHttpResponse response) {
		doGet(request,response);
	}
	
	

}
