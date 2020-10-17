package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder2 extends ByteToMessageDecoder {

    private int nextMassageSize;

    private final ByteBuf storedByteBuf = PooledByteBufAllocator.DEFAULT.buffer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) {

        this.storedByteBuf.writeBytes(in);

        if (this.storedByteBuf.readableBytes() >= 4 && nextMassageSize == 0) {
            this.nextMassageSize = this.storedByteBuf.readInt();
        }

        while (this.storedByteBuf.readableBytes() >= this.nextMassageSize && this.nextMassageSize != 0) {
            ByteBuf read = this.storedByteBuf.readBytes(this.nextMassageSize);
            out.add(read);
            if (this.storedByteBuf.readableBytes() >= 4) {
                this.nextMassageSize = this.storedByteBuf.readInt();
            } else {
                this.nextMassageSize = 0;
                break;
            }
        }

        System.out.println("storedbytes: " + this.storedByteBuf.readableBytes());

    }
}
