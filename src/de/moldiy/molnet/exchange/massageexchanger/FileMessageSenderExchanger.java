package de.moldiy.molnet.exchange.massageexchanger;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.utils.MapArray;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class FileMessageSenderExchanger {

    private final MapArray<Channel, FileTransfer> filesToSend = new MapArray<>();

    public synchronized void sendFile(Channel channel, String path, String file) throws FileNotFoundException {
        FileTransfer fileMessage = new FileTransfer(channel, path, new File(file));
        this.filesToSend.put(channel, fileMessage);
        this.sendNewFile(channel);
    }

    @TrafficID(id = "molnet.file.request")
    private void sendFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf message) throws IOException {

        List<FileTransfer> transferList = this.filesToSend.get(ctx.channel());

        FileTransfer fileTransfer = transferList.get(0);

        byte[] read = new byte[8192]; // 4096 | 8192
        int readSize = fileTransfer.read(read);

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(read, 0, readSize);

        networkInterface.writeAndFlush(ctx.channel(), "molnet.file.write", byteBuf);

        if (readSize < read.length) {
            fileTransfer.close();
            transferList.remove(0);
            this.sendNewFile(ctx.channel());
        }
    }

    private void sendNewFile(Channel channel) throws FileNotFoundException {
        List<FileTransfer> transferList = this.filesToSend.get(channel);
        if(!transferList.isEmpty()) {
            FileTransfer fileTransfer = transferList.get(0);
            if (fileTransfer != null && !fileTransfer.isOpen()) {
                fileTransfer.open();

                ByteBuf firstMessageByteBuf = channel.alloc().buffer();
                NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, "molnet.file.new");
                NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, fileTransfer.getPath());
                firstMessageByteBuf.writeLong(fileTransfer.getTotalFileSize());
                channel.writeAndFlush(firstMessageByteBuf);
            }
        }
    }

}
