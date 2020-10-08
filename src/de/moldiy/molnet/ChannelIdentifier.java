package de.moldiy.molnet;

import io.netty.channel.Channel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChannelIdentifier {

    protected final Map<Channel, Object> valueIdentifier = Collections.synchronizedMap(new HashMap<>());
    protected final Map<Object, Channel> identifierValue = Collections.synchronizedMap(new HashMap<>());

    public ChannelIdentifier() {

    }

    public void addChannel(Channel ctx, Object value) {
        if (!this.valueIdentifier.containsKey(ctx)) {
            this.valueIdentifier.put(ctx, value);
            this.identifierValue.put(value, ctx);
        }
    }

    public void removeChannel(Channel ctx) {
        Object value = this.valueIdentifier.remove(ctx);
        this.identifierValue.remove(value);
    }

    public Channel getChannel(Object value) {
        return this.identifierValue.get(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getIdentifier(Channel ctx) {
        return (T) this.valueIdentifier.get(ctx);
    }

    public Map<Channel, Object> getValueIdentifier() {
        return valueIdentifier;
    }

    public Map<Object, Channel> getIdentifierValue() {
        return identifierValue;
    }
}
