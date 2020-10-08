package de.moldiy.molnet;

import de.moldiy.molnet.utils.BitVector;
import io.netty.channel.ChannelHandlerContext;

public class ServerMessageHandler extends MessageHandler {

    @Override
    public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
        return super.getNetworkInterface().getChannelIdentifierManager().getIdentifier("rights", ctx.channel());
    }

    @Override
    protected void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id) {
        System.out.println("[Server] TrafficID not found " + id + " sender: " + ctx.name());
    }

    @Override
    protected void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx, BitVector rightBits) {
        System.out.println("[Server] no Access To the <"+ trafficID +"> trafficID. Ctx: " + ctx.channel().id() +
                " with the rights: " + rightBits);
    }
}
