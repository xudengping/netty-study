package xdp.test.netty.catalina.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import org.apache.log4j.Logger;

public class DPTomcat {

	private static Logger LOG = Logger.getLogger(DPTomcat.class);

	private void start(int port) throws Exception {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup worker = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstarp = new ServerBootstrap();
			bootstarp
					.group(boss, worker)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel client)
								throws Exception {
							ChannelPipeline pipeline = client.pipeline();

                            // 服务端对客户端请求消息解密
							pipeline.addLast(new HttpRequestDecoder());

							// 服务端发送消息到客户端加密
							pipeline.addLast(new HttpResponseEncoder());

							// 正真处理业务逻辑
							pipeline.addLast(new DPTomcatHandler());

						}
					}).option(ChannelOption.SO_BACKLOG, 128)
					.childOption(ChannelOption.SO_KEEPALIVE, true);


			// 阻塞等待服务端绑定端口完成
			ChannelFuture future = bootstarp.bind(port).sync();
			LOG.info("HTTP服务器启动，绑定端口:" + port);


			LOG.info("HTTP开始接受客户端请求");
			future.channel().closeFuture().sync();

		} finally{
			boss.shutdownGracefully();
			worker.shutdownGracefully();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new DPTomcat().start(8080);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
