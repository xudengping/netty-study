$(document).ready(function(){
	var host = location.href.replace(/http:\/\//i,"");
	window.CHAT = {
			// 保存服务端websocket的请求地址
			serverAddr:"ws://"+host+"im",
			// 保存浏览器socket对象
	        socket:null,
	        
	        init:function(){
	        	// 浏览器兼容问题
				if(!window.WebSocket){
					window.WebSocket = window.MozWebSocket;
				}
				// 支持websocket协议
				if(window.WebSocket){
					alert(CHAT.serverAddr);
					CHAT.socket = new WebSocket(CHAT.serverAddr);
					
					CHAT.socket.onmessage = function(e){
						alert(e.data);
					}
					
					CHAT.socket.onopen = function(e){
						alert("WebSocket开启");
						CHAT.socket.send("登录了");
					}
					
					CHAT.socket.onclose = function(e){
						alert("WebSocket关闭");
					}
					
				}else{
					alert("你的浏览器不支持 WebSocket！");
				}
	        },
			
			login:function(){
				$("#loginbox").hide();
				$("#chatbox").show();
				CHAT.init();
			}
	}
});