package de.moldiy.molnet.server;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;
import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class AuthenticateValidatorHandler extends SimpleChannelInboundHandler<ByteBuf> {

//	private ChannelGroup spaceServer = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private final Json j = new Json();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
			// TODO ich weis noch nicht ob das Thread safe ist muss vieleicht aendern.

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
				args = j.fromJson(String[].class, loginString);
			} catch (SerializationException e) {
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

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

}
