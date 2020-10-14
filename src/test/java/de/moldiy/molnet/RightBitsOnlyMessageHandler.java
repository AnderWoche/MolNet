package de.moldiy.molnet;

import de.moldiy.molnet.utils.BitVector;
import io.netty.channel.ChannelHandlerContext;

public abstract class RightBitsOnlyMessageHandler extends MessageHandler {

    @Override
    protected void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id) {
    }

    @Override
    protected void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx) {

    }
}
