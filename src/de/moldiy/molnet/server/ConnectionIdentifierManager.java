package de.moldiy.molnet.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.*;

public class ConnectionIdentifierManager {

    private List<ConnectionIdentifier> identifierList = Collections.synchronizedList(new ArrayList<>());
    private Map<String, ConnectionIdentifier> identifier = Collections.synchronizedMap(new HashMap<>());

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
        ConnectionIdentifier connectionIdentifier = this.identifier.get(identifierName);
        if (connectionIdentifier == null) {
            throw new IdentifierNotExistException("A ConnectionIdentifier with the name " + identifierName +
                    " is not registered");
        }
        return (T) connectionIdentifier.getIdentifierFromIdentifier(ctx);
    }

    public Map<String, ConnectionIdentifier> getIdentifier() {
        return identifier;
    }

    public List<ConnectionIdentifier> getIdentifierList() {
        return identifierList;
    }

    public static class IdentifierNotExistException extends RuntimeException {
        public IdentifierNotExistException(String message) {
            super(message);
        }
    }
}
