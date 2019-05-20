package xdp.test.netty.project.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

/**
 * @author xudengping
 * @version 1.0
 * @description 简单的输入handler
 * @date 2019-05-20
 */
public class SimpleHandler extends ChannelInboundHandlerAdapter {

    /**
     *  会存在多次调用，一次完整解析会触发调用一次
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("========读取客户端数据=========");
        if(msg instanceof ByteBuf){
            ByteBuf req = (ByteBuf) msg;
            String content = req.toString(Charset.defaultCharset());
            System.out.println("客户端消息: "+content);

            // 服务端返回数据给客户端
            System.out.println("##########发生消息给客户端##########");
            ctx.channel().writeAndFlush("里斯\r\n小明\r\n");
        }
    }
}
