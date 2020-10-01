package de.moldiy.molnet.server.authenticate;

import io.netty.channel.ChannelHandlerContext;

public class AuthenticateEvent {

    public ChannelHandlerContext ctx;

    public String[] args;

    public AuthenticateEvent(ChannelHandlerContext ctx, String[] args) {
        this.ctx = ctx;
        this.args = args;
    }

}
