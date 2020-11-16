package de.moldiy.molnet.blockingtest;

import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ClientMessageExchanger {

    @TrafficID(id = "response")
    public void response(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        System.out.println("DONE!");
    }
}
