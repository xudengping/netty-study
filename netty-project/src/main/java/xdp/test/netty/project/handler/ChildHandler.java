package xdp.test.netty.project.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;

/**
 * @author xudengping
 * @version 1.0
 * @description 简单的输出handler
 * @date 2019-05-20
 */
public class ChildHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("=======客户端接受到服务端数据=======");

        // 将服务端数据保存起来待后续处理
        System.out.println(ctx.channel().attr(AttributeKey.valueOf("ChannelKey")).get());
        ctx.channel().attr(AttributeKey.valueOf("ChannelKey")).set(msg.toString());

        // 短链接
        ctx.close();

    }
}
