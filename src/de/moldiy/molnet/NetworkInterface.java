package de.moldiy.molnet;

import de.moldiy.molnet.exchange.RightIDFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public abstract class NetworkInterface implements MessageWriter {

    protected final RightIDFactory rightIDFactory = new RightIDFactory(false);

    protected final ChannelIdentifierManager channelIdentifierManager = new ChannelIdentifierManager();

    public <T> Channel getChannel(String identifierName, T value) {
        return this.channelIdentifierManager.getChannel(identifierName, value);
    }

    public <T> void write(String identifierName, T vale, String trafficID, ByteBuf message) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, vale);
        this.write(channel, trafficID, message);
    }

    public <T> void writeAndFlush(String identifierName, T vale, String trafficID, ByteBuf message) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, vale);
        this.writeAndFlush(channel, trafficID, message);
    }

    public RightIDFactory getRightIDFactory() {
        return rightIDFactory;
    }

    public ChannelIdentifierManager getChannelIdentifierManager() {
        return channelIdentifierManager;
    }
}
