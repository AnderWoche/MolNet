package de.moldiy.molnet;

import de.moldiy.molnet.utils.BitVector;
import io.netty.channel.ChannelHandlerContext;

public class DefaultMessageHandler extends MessageHandler {

    @Override
    public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
        return null;
    }

    @Override
    protected void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id) {
        System.out.println("["+this.getNetworkInterfaceClassName(super.getNetworkInterface())+"] traffic ID "+ id +" not found.");
    }

    @Override
    protected void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx) {
        System.out.println("["+this.getNetworkInterfaceClassName(super.getNetworkInterface())+"] The Channel " + ctx.channel() + " has no access to " + trafficID);
    }

    public String getNetworkInterfaceClassName(NetworkInterface networkInterface) {
        if(networkInterface instanceof Client) {
            return Client.class.getSimpleName();
        } else {
            return Server.class.getSimpleName();
        }
    }
}
