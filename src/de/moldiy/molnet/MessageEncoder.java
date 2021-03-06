package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
		int massageLength = msg.readableBytes();
		
		out.writeInt(massageLength);
		out.writeBytes(msg);
	}

}
