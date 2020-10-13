package de.moldiy.molnet;

import de.moldiy.molnet.exchange.RightIDFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.DefaultFileRegion;

import java.io.*;
import java.nio.channels.FileChannel;

public abstract class NetworkInterface implements MessageWriter {

    protected final RightIDFactory rightIDFactory = new RightIDFactory(false);

    protected final ChannelIdentifierManager channelIdentifierManager = new ChannelIdentifierManager();

    public <T> Channel getChannel(String identifierName, T value) {
        return this.channelIdentifierManager.getChannel(identifierName, value);
    }

    public <T> void write(String identifierName, T value, String trafficID, ByteBuf message) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.write(channel, trafficID, message);
    }

    public <T> void writeAndFlush(String identifierName, T value, String trafficID, ByteBuf message) {
        Channel channel = this.channelIdentifierManager.getChannel(identifierName, value);
        this.writeAndFlush(channel, trafficID, message);
    }

    public void writeFile(Channel channel, String path, File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        ByteBuf firstMessageByteBuf = channel.alloc().buffer();
        NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, "molnet.file");
        NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, path);
        firstMessageByteBuf.writeLong(file.length());
        channel.writeAndFlush(firstMessageByteBuf);

        int readSize;
        byte[] read = new byte[4096]; // 4096 | 8192
        while ((readSize = fileInputStream.read(read)) > 0) {
            ByteBuf byteBuf = channel.alloc().buffer();
            NettyByteBufUtil.writeUTF16String(byteBuf, "molnet.file");
            byteBuf.writeBytes(read, 0, readSize);
            channel.writeAndFlush(byteBuf);
        }

    }

    public RightIDFactory getRightIDFactory() {
        return rightIDFactory;
    }

    public ChannelIdentifierManager getChannelIdentifierManager() {
        return channelIdentifierManager;
    }
}
