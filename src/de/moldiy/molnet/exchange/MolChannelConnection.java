package de.moldiy.molnet.exchange;

import io.netty.channel.ChannelHandlerContext;

public abstract class MolChannelConnection {

    protected ChannelHandlerContext ctx;

    public MolChannelConnection(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
}
