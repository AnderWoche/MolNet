package de.moldiy.molnet;

import de.moldiy.molnet.exchange.RightIDFactory;
import de.moldiy.molnet.exchange.massageexchanger.FileMessageSenderExchanger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class NetworkInterface implements MessageWriter {

    protected final MessageHandler messageHandler;

    protected final RightIDFactory rightIDFactory = new RightIDFactory(false);

    protected final ChannelIdentifierManager channelIdentifierManager = new ChannelIdentifierManager();

    public NetworkInterface(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void loadMessageExchanger(Object object) {
        this.messageHandler.loadMessageExchanger(object);
    }

    public <T> T getMessageExchanger(Class<T> objectClass) {
        return this.messageHandler.getMessageExchangerManager().getMassageExchanger(objectClass);
    }

    public <T> Channel getChannel(String identifierName, T value) {
        return this.channelIdentifierManager.getChannel(identifierName, value);
    }

    public <T> void write(String identifierName, T value, String trafficID, ByteBuf message) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.write(channel, trafficID, message);
    }

    public <T> void write(String identifierName, T value, String trafficID) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.write(channel, trafficID, channel.alloc().buffer());
    }

    public <T> void writeAndFlush(String identifierName, T value, String trafficID, ByteBuf message) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.writeAndFlush(channel, trafficID, message);
    }

    public <T> void writeAndFlush(String identifierName, T value, String trafficID) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.writeAndFlush(channel, trafficID, channel.alloc().buffer());
    }

    @Override
    public void writeFile(Channel channel, String path, String file) throws FileNotFoundException {
        this.getMessageExchanger(FileMessageSenderExchanger.class).sendFile(channel, path, file);
    }

    public abstract void broadcastFile(String path, String file) throws IOException;

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public RightIDFactory getRightIDFactory() {
        return rightIDFactory;
    }

    public ChannelIdentifierManager getChannelIdentifierManager() {
        return channelIdentifierManager;
    }
}
