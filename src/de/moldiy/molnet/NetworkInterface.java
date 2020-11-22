package de.moldiy.molnet;

import de.moldiy.molnet.exchange.RightIDFactory;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.ActiveFileProviderExchanger;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.ActiveFileReaderExchanger;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.FileDownloadProcessor;
import de.moldiy.molnet.exchange.massageexchanger.file.passive.PassiveFileSenderExchanger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;

public abstract class NetworkInterface implements AdvancedMessageWriter {

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
        this.getMessageExchanger(PassiveFileSenderExchanger.class).sendFile(channel, path, file);
    }

    public void provideFile(String name, Path path) throws IOException {
        this.getMessageExchanger(ActiveFileProviderExchanger.class).provide(name, path);
    }

    public FileDownloadProcessor requestFile(Channel channel, String name, Path directory) {
        return this.getMessageExchanger(ActiveFileReaderExchanger.class).requestFile(this, channel, name, directory);
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
