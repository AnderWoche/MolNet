package de.moldiy.molnet;

import de.moldiy.molnet.utils.BitVector;
import io.netty.channel.ChannelHandlerContext;

public class EmptyMessageHandler extends MessageHandler {

    @Override
    public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
        return null;
    }

    @Override
    protected void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id) {

    }

    @Override
    protected void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx) {

    }

}
