package xdp.test.netty.project;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import xdp.test.netty.project.handler.ChildHandler;

import java.nio.charset.Charset;

/**
 * @author xudengping
 * @version 1.0
 * @description 一个完整的服务端客户端信息交互案例-客户端
 * @date 2019-05-20
 */
public class NettyClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            // 以\r\n作为结束标示
                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE,Delimiters.lineDelimiter()[0]));

                            // 对接收到的数据进行解码
                            channel.pipeline().addLast(new StringDecoder());

                            // 处理服务端数据
                            channel.pipeline().addLast(new ChildHandler());

                            // 对输出数据进行编码
                            channel.pipeline().addLast(new StringEncoder());
                        }
                    });

            ChannelFuture future = bootstrap.connect("localhost", 8080).sync();
            System.out.println("客户端链接到8080端口");

            String name = "张三\r\n";
            future.channel().writeAndFlush(name);

            // 同理StringEncoder实现
//            ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer();
//            buffer.writeBytes(name.getBytes(Charset.defaultCharset()));
//            future.channel().writeAndFlush(buffer);

            // 同步等待客户端关闭链接
            future.channel().closeFuture().sync();

            Object msg = future.channel().attr(AttributeKey.valueOf("ChannelKey")).get();
            System.out.println("接收到的服务端数据："+msg);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}
