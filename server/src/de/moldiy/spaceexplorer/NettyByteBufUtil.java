package de.moldiy.spaceexplorer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NettyByteBufUtil {

	public static final Charset UTF16Charset = StandardCharsets.UTF_16;

	public static void writeUTF16String(ByteBuf byteBuf, String s) {
		ByteBuf stringByteBuffer = ByteBufAllocator.DEFAULT.buffer();

		stringByteBuffer.writeCharSequence(s, UTF16Charset);

		byteBuf.writeInt(stringByteBuffer.readableBytes());
		byteBuf.writeBytes(stringByteBuffer);

		stringByteBuffer.release();

	}

	public static String readUTF16String(ByteBuf byteBuf) {
		int length = byteBuf.readInt();
		return (String) byteBuf.readCharSequence(length, UTF16Charset);
	}

	public static ByteBuf addStringBeforeMassage(String string, ByteBuf byteBuf) {
		ByteBuf stringByteBuffer = ByteBufAllocator.DEFAULT.buffer();
		writeUTF16String(stringByteBuffer, string);

		stringByteBuffer.writeBytes(byteBuf);

		byteBuf.clear();
		byteBuf.writeBytes(stringByteBuffer);

		stringByteBuffer.release();

		return byteBuf;
	}

}
