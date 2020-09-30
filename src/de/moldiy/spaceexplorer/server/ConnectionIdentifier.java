package de.moldiy.spaceexplorer.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

public abstract class ConnectionIdentifier<T> {

    protected HashMap<ChannelHandlerContext, T> valueIdentifier = new HashMap<>();
    protected HashMap<T, ChannelHandlerContext> identifierValue = new HashMap<>();

    public ConnectionIdentifier() {

    }

    public void addChannel(ChannelHandlerContext ctx, String... args) {
        T value = this.initIdentifier(ctx, args);
        this.valueIdentifier.put(ctx, value);
        this.identifierValue.put(value, ctx);
    }

    public void removeChannel(ChannelHandlerContext ctx) {
        T value = this.valueIdentifier.remove(ctx);
        this.identifierValue.remove(value);
    }

    protected abstract T initIdentifier(ChannelHandlerContext ctx, String... args);

    public ChannelHandlerContext getChannelFromIdentifier(T value) {
        return this.identifierValue.get(value);
    }

    public T getIdentifierFromIdentifier(ChannelHandlerContext ctx) {
        return this.valueIdentifier.get(ctx);
    }
}
