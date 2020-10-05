package de.moldiy.molnet.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionIdentifierManager {

    private List<ConnectionIdentifier> identifierList = new ArrayList<>();
    private HashMap<String, ConnectionIdentifier> identifier = new HashMap<>();

    public void registerConnectionIdentifier(String identifierName, ConnectionIdentifier<?> connectionIdentifier) {
        this.identifierList.add(connectionIdentifier);
        this.identifier.put(identifierName, connectionIdentifier);
    }

    public void addConnection(ChannelHandlerContext ctx, String... args) {
        for (int i = identifierList.size() - 1; i >= 0; i--) {
            this.identifierList.get(i).addChannel(ctx, args);
        }
    }

    public void removeConnection(ChannelHandlerContext ctx) {
        for (int i = identifierList.size() - 1; i >= 0; i--) {
            this.identifierList.get(i).removeChannel(ctx);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ChannelHandlerContext getConnectionFromIdentifier(String identifierName, T value) {
        return this.identifier.get(identifierName).getChannelFromIdentifier(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getIdentifierFromConnection(String identifierName, ChannelHandlerContext ctx) {
        return (T) this.identifier.get(identifierName).getIdentifierFromIdentifier(ctx);
    }

    public HashMap<String, ConnectionIdentifier> getIdentifier() {
        return identifier;
    }

    public List<ConnectionIdentifier> getIdentifierList() {
        return identifierList;
    }
}
