package de.moldiy.molnet.client;

import com.badlogic.gdx.utils.Json;
import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AuthenticateHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final Json j = new Json();
    private ChannelHandlerContext ctx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.handlerAdded(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        byte success = msg.readByte();
        if (success == 1) {
            this.loginSuccessful(ctx);
            ctx.pipeline().remove(this);
            return;
        }
        this.loginUnSuccessful(ctx);
    }

    public void authenticate(String... args) {
        ByteBuf byteBuf = this.ctx.alloc().buffer();
        String msg = j.toJson(args);

        NettyByteBufUtil.writeUTF16String(byteBuf, msg);

        ctx.writeAndFlush(byteBuf);
    }

    public abstract void loginSuccessful(ChannelHandlerContext ctx);

    public abstract void loginUnSuccessful(ChannelHandlerContext ctx);
}
