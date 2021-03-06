package de.moldiy.molnet;

import de.moldiy.molnet.exchange.DTOSerializer;
import de.moldiy.molnet.exchange.RightIDFactory;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.ProviderFileExchanger;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.ProviderFileReaderExchanger;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.FileDownloadProcessor;
import de.moldiy.molnet.exchange.massageexchanger.file.passive.PassiveFileSenderExchanger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public abstract class NetworkInterface implements NetworkInterfaceMessageWriter {

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

    public Collection<Object> getAllMessageExchanger() {
        return this.messageHandler.getMessageExchangerManager().getAllMessageExchanger();
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
    public <A, T extends DTOSerializer> void writeAndFlush(String identifierName, A value, String trafficID, T dto) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.writeAndFlush(channel, trafficID, dto);
    }

    @Override
    public void writeFile(Channel channel, String path, String file) throws FileNotFoundException {
        this.getMessageExchanger(PassiveFileSenderExchanger.class).sendFile(channel, path, file);
    }

    public void provideFile(String name, Path path) throws IOException {
        this.getMessageExchanger(ProviderFileExchanger.class).provide(name, path);
    }

    public FileDownloadProcessor requestFile(Channel channel, String name, Path directory) {
        return this.getMessageExchanger(ProviderFileReaderExchanger.class).requestFile(this, channel, name, directory);
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

    public boolean isServer() {
        return this instanceof Server;
    }

    public boolean isClient() {
        return this instanceof Client;
    }
}
