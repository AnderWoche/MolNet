package de.moldiy.molnet;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;

public class MessageDecoder extends ReplayingDecoder<ByteBuf> {

    private final static int MAX_MASSSAGE_SIZE = 500_000; // this is 500KB

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

        int nextMassageSize = in.readInt();

        if (nextMassageSize > MAX_MASSSAGE_SIZE) {
            throw new MaximumSizeExceeded("A Massage is Bigger than " + MAX_MASSSAGE_SIZE
                    + "Bytes. That's to much. Use File Transfer if this is a File");
        }

        out.add(in.readBytes(nextMassageSize));
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
