package xdp.test.netty.chat.server;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import xdp.test.netty.chat.handler.HttpHandler;
import xdp.test.netty.chat.handler.WebSocketHandler;

public class ChatServer {
	
	
	private static Logger LOG = Logger.getLogger(ChatServer.class);
	
	private void start(int port) throws InterruptedException {
		EventLoopGroup boss = new NioEventLoopGroup();//cup两倍的线程数
		EventLoopGroup worker = new NioEventLoopGroup();//cup两倍的线程数
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		
		try {
			serverBootstrap.group(boss,worker)
			               .channel(NioServerSocketChannel.class)
			               .option(ChannelOption.SO_BACKLOG, 1024)
			               .childHandler(new ChannelInitializer<SocketChannel>() {

								@Override
								protected void initChannel(SocketChannel client) throws Exception {
									ChannelPipeline pipeline = client.pipeline();
									
									// =========支持HTTP协议=================
									pipeline.addLast(new HttpServerCodec());
									
									// 在使用了SimpleChannelInboundHandler时，需要设置最大的接收http头信息大小
									pipeline.addLast(new HttpObjectAggregator(64*1024));
									
									// 用于处理写文件流
									pipeline.addLast(new ChunkedWriteHandler());
									
									// 处理业务逻辑
									pipeline.addLast(new HttpHandler());
									
									// ===========支持WebSocket协议========================
									pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
									pipeline.addLast(new WebSocketHandler());
									
									
								}
						});
			
			ChannelFuture future = serverBootstrap.bind(port).sync();
			LOG.info("服务已启动,监听端口" + port);
			future.channel().closeFuture().sync();
		}finally {
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		new ChatServer().start(8080);
	}

}
