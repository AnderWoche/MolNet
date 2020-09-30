package de.moldiy.molnet.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

public class ConnectionIdentifierManager {

    private List<ConnectionIdentifier> identifier = new ArrayList<>();

    public void registerConnectionIdentifier(ConnectionIdentifier connectionIdentifier) {
        this.identifier.add(connectionIdentifier);
    }

    public void addConnection(ChannelHandlerContext ctx, String... args) {
        for (int i = 0; i < identifier.size(); i++) {
            this.identifier.get(i).addChannel(ctx, args);
        }
    }

    public void removeConnection(ChannelHandlerContext ctx) {
        for (int i = 0; i < identifier.size(); i++) {
            this.identifier.get(i).removeChannel(ctx);
        }
    }

    public <T> ChannelHandlerContext getConnectionFromIdentifier(T value) {
        for (int i = 0; i < identifier.size(); i++) {
            ChannelHandlerContext ctx = this.identifier.get(i).getChannelFromIdentifier(value);
            if (ctx != null) return ctx;
        }
        return null;
    }


}
