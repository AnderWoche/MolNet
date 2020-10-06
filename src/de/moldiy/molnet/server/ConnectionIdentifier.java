package de.moldiy.molnet.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ConnectionIdentifier<T> {

    protected final Map<ChannelHandlerContext, T> valueIdentifier = Collections.synchronizedMap(new HashMap<>());
    protected final Map<T, ChannelHandlerContext> identifierValue = Collections.synchronizedMap(new HashMap<>());

    public ConnectionIdentifier() {

    }

    public void addChannel(ChannelHandlerContext ctx, String... args) {
        T value = this.initIdentifier(ctx, args);
        if (!this.valueIdentifier.containsKey(ctx)) {
            this.valueIdentifier.put(ctx, value);
            this.identifierValue.put(value, ctx);
        }
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

    public Map<ChannelHandlerContext, T> getValueIdentifier() {
        return valueIdentifier;
    }

    public Map<T, ChannelHandlerContext> getIdentifierValue() {
        return identifierValue;
    }
}
