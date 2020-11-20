package de.moldiy.molnet.exchange.massageexchanger.file.active;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.nio.file.Path;

public class FileDownloadProcessor {

    private NetworkInterface networkInterface;
    private Channel channel;

    private final String name;
    private final Path directory;

    public FileDownloadProcessor(NetworkInterface networkInterface, Channel channel, String name, Path directory) {
        this.networkInterface = networkInterface;
        this.channel = channel;
        this.name = name;
        this.directory = directory;
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public Path getDirectory() {
        return directory;
    }


    public void write(String trafficID, ByteBuf message) {
        this.channel.write(NettyByteBufUtil.addStringBeforeMassage(trafficID, message));
    }

    public void write(String trafficID) {
        this.channel.write(NettyByteBufUtil.addStringBeforeMassage(trafficID, this.channel.alloc().buffer()));

    }

    public void writeAndFlush(String trafficID, ByteBuf message) {
        this.write(trafficID, message);
        this.channel.flush();
    }

    public void writeAndFlush(String trafficID) {
        this.writeAndFlush(trafficID, this.channel.alloc().buffer());

    }

    public void flush() {
        this.channel.flush();
    }
}
