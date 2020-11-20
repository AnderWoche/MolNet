package de.moldiy.molnet.exchange.massageexchanger.file.active;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.exchange.massageexchanger.file.FileExchangerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ActiveFileReaderExchanger {

    private final List<FileDownloadProcessor> processors = new ArrayList<>();
    private FileDownloadProcessor activeProcessor;

    private FileOutputStream fileOutputStream;

    public void requestFile(NetworkInterface networkInterface, Channel channel, String name, Path directory) {
        this.processors.add(new FileDownloadProcessor(networkInterface, channel, name, directory));
        this.requestNewFilePacket();
    }

    private void requestNewFilePacket() {
        if(this.activeProcessor != null) {
            this.activeProcessor = this.processors.get(0);

            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
            NettyByteBufUtil.writeUTF16String(byteBuf, this.activeProcessor.getName());
            this.activeProcessor.write(FileExchangerConstants.PASSIVE_FILE_SENDER_REQUEST, byteBuf);
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_PACKET)
    public void net_newFilePacket(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {

    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_FILE)
    public void net_newFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {

    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_WRITE)
    private void net_writeFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        byte[] readBytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(readBytes);

        this.fileOutputStream.write(readBytes);
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_CLOSE)
    public void net_close(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        if(this.fileOutputStream != null) {
            this.fileOutputStream.close();
        }
    }
}
