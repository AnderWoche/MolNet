package de.moldiy.spaceexplorer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MassageWriter extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
		int massageLength = msg.readableBytes();
		
		out.writeInt(massageLength);
		out.writeBytes(msg);
	}

}
