package de.moldiy.molnet;

import io.netty.channel.ChannelHandlerContext;

public interface ChannelConnectedListener {
    void channelConnected(ChannelHandlerContext ctx);
}
