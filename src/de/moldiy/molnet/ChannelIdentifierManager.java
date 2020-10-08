package de.moldiy.molnet;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChannelIdentifierManager {

    private final Map<String, ChannelIdentifier> identifier = Collections.synchronizedMap(new HashMap<>());

    private ChannelIdentifier registerChannelIdentifier(String identifierName) {
        ChannelIdentifier channelIdentifier = new ChannelIdentifier();
        this.identifier.put(identifierName, channelIdentifier);
        return channelIdentifier;
    }

    public <T> void setIdentifier(Channel ctx, String identifierName, T value) {
        ChannelIdentifier channelIdentifier = this.getChannelIdentifier(identifierName);
        channelIdentifier.addChannel(ctx, value);
    }

    public void removeIdentifier(Channel ctx, String identifierName) {
        ChannelIdentifier channelIdentifier = this.getChannelIdentifier(identifierName);
        channelIdentifier.removeChannel(ctx);
    }

    public ChannelIdentifier getChannelIdentifier(String identifierName) {
        ChannelIdentifier connectionIdentifier = this.identifier.get(identifierName);
        if (connectionIdentifier == null) {
            connectionIdentifier = this.registerChannelIdentifier(identifierName);
        }
        return connectionIdentifier;
    }

    public <T> Channel getChannel(String identifierName, T value) {
        return this.getChannelIdentifier(identifierName).getChannel(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getIdentifier(String identifierName, Channel ctx) {
        ChannelIdentifier channelIdentifier = this.getChannelIdentifier(identifierName);
        return (T) channelIdentifier.getIdentifier(ctx);
    }

    public Map<String, ChannelIdentifier> getAllIdentifier() {
        return identifier;
    }
}
