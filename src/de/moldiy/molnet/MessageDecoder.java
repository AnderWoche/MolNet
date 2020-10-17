package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class MessageDecoder extends ReplayingDecoder<ByteBuf> {

    public static final int MAX_MESSAGE_SIZE = 500_000;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int nextMassageSize = in.readInt();

        if (nextMassageSize > MAX_MESSAGE_SIZE) {
            System.out.println("A Massage is Bigger than " + MAX_MESSAGE_SIZE
                    + "Bytes. Disconnecting The Channel...");
            ctx.close();
        }

        ByteBuf read = in.readBytes(nextMassageSize);
        out.add(read);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause instanceof DecoderException) {
            cause.printStackTrace();
            ctx.close();
            return;
        }
        super.exceptionCaught(ctx, cause);
    }

    public static class MaximumSizeExceeded extends RuntimeException {
        private static final long serialVersionUID = -1833615422763957637L;

        public MaximumSizeExceeded(String s) {
            super(s);
        }
    }

}
