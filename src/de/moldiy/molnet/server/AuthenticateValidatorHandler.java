package de.moldiy.molnet.server;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AuthenticateValidatorHandler extends SimpleChannelInboundHandler<ByteBuf> {

//	private ChannelGroup spaceServer = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private final Gson j = new Gson();

	private final EventBus eventBus = new EventBus();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {

			String loginString;
			try {
				loginString = NettyByteBufUtil.readUTF16String(msg);
			} catch (IndexOutOfBoundsException e) {
				this.sendLoginUnSuccessfulMassage(ctx);
				this.loginUnSuccessful(ctx);
				return;
			}

			String[] args;
			try {
				args = j.fromJson(loginString, String[].class);
			} catch (JsonSyntaxException e) {
				this.sendLoginUnSuccessfulMassage(ctx);
				this.loginUnSuccessful(ctx);
				return;
			}

			if(this.authenticate(args)) {
				this.sendLoginSuccessfulMassage(ctx);
				this.loginSuccessful(ctx, args);
				ctx.pipeline().remove(this);
			} else {
				this.sendLoginUnSuccessfulMassage(ctx);
				this.loginUnSuccessful(ctx);
			}

	}

	public abstract boolean authenticate(String[] args);

	private void sendLoginSuccessfulMassage(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(ctx.alloc().buffer().writeByte((byte) 1));
	}

	protected void sendLoginUnSuccessfulMassage(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(ctx.alloc().buffer().writeByte((byte) 0));
	}

	public abstract void loginSuccessful(ChannelHandlerContext ctx, String[] args);

	public abstract void loginUnSuccessful(ChannelHandlerContext ctx);

	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

}
