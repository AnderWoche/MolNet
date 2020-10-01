package de.moldiy.molnet.client;

import com.google.gson.Gson;
import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthenticateHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final Gson j = new Gson();

    private ChannelHandlerContext ctx;

    private boolean successfulAuthenticated = false;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.successfulAuthenticated = false;
        super.handlerAdded(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        byte success = msg.readByte();
        if (success == 1) {
            this.loginSuccessful(ctx);
            this.successfulAuthenticated = true;
            ctx.pipeline().remove(this);
            return;
        }
        this.loginUnSuccessful(ctx);
    }

    public void authenticate(String... args) {
        if(successfulAuthenticated) throw new AlreadySuccessfulAuthenticated("Authentication was already successful");
        ByteBuf byteBuf = this.ctx.alloc().buffer();
        String msg = j.toJson(args);

        NettyByteBufUtil.writeUTF16String(byteBuf, msg);

        ctx.writeAndFlush(byteBuf);
    }

    public void loginSuccessful(ChannelHandlerContext ctx) {
    }

    public void loginUnSuccessful(ChannelHandlerContext ctx) {
    }

    public static class AlreadySuccessfulAuthenticated extends RuntimeException {
        public AlreadySuccessfulAuthenticated(String s) {
            super(s);
        }
    }
}
