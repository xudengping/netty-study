package xdp.test.netty.project;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.log4j.Logger;
import xdp.test.netty.project.handler.SimpleHandler;

/**
 * @author xudengping
 * @version 1.0
 * @description 一个完整的服务端客户端信息交互案例-服务类
 * @date 2019-05-20
 */
public class NettyServer {
    private static Logger LOG = Logger.getLogger(NettyServer.class);

    public static void main(String[] args) {
        // acceptor线程池用于接收服务端、客户端的tcp连接请求
        EventLoopGroup boss = new NioEventLoopGroup();
        // 网络io操作线程池
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128) // 阻塞队列大小
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            // 设置以\r\n作为结束标示
                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));

                            // 处理客户端数据
                            channel.pipeline().addLast(new SimpleHandler());

                            // 对输出数据编码
                            channel.pipeline().addLast(new StringEncoder());
                        }
                    });

            ChannelFuture future = serverBootstrap.bind(8080).sync();
            LOG.info("服务器启动，绑定端口:8080");
            LOG.info("开始接受客户端请求");

            // 同步阻塞等待服务关闭
            future.channel().closeFuture().sync();
            LOG.info("服务器关闭");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
