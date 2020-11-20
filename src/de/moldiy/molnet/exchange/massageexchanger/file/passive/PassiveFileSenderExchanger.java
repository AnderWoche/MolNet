package de.moldiy.molnet.exchange.massageexchanger.file.passive;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.exchange.massageexchanger.file.FileExchangerConstants;
import de.moldiy.molnet.utils.MapArray;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class PassiveFileSenderExchanger {

    private final MapArray<Channel, FilePassiveTransfer> filesToSend = new MapArray<>();

    public synchronized void sendFile(Channel channel, String path, String file) throws FileNotFoundException {
        File f = new File(file);
        if(!f.isFile()) throw new FileNotFoundException("The path: " + file + " is not a file.");
        FilePassiveTransfer fileMessage = new FilePassiveTransfer(channel, path, f);
        this.filesToSend.put(channel, fileMessage);
        this.sendNewFile(channel);
    }

    @TrafficID(id = FileExchangerConstants.PASSIVE_FILE_SENDER_REQUEST)
    private void net_sendFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf message) throws IOException {

        List<FilePassiveTransfer> transferList = this.filesToSend.get(ctx.channel());

        FilePassiveTransfer fileTransfer = transferList.get(0);

        byte[] read = new byte[FileExchangerConstants.SAVED_FILE_BYTES_IN_RAM];
        int readSize = fileTransfer.read(read);

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(read, 0, readSize);

        networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.PASSIVE_FILE_RECEIVER_WRITE, byteBuf);

        if (readSize < read.length) {
            fileTransfer.close();
            transferList.remove(0);
            this.sendNewFile(ctx.channel());
        }
    }

    private void sendNewFile(Channel channel) throws FileNotFoundException {
        List<FilePassiveTransfer> transferList = this.filesToSend.get(channel);
        if(!transferList.isEmpty()) {
            FilePassiveTransfer fileTransfer = transferList.get(0);
            if (fileTransfer != null && !fileTransfer.isOpen()) {
                fileTransfer.open();

                ByteBuf firstMessageByteBuf = channel.alloc().buffer();
                NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, FileExchangerConstants.PASSIVE_FILE_RECEIVER_NEW);
                NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, fileTransfer.getPath());
                firstMessageByteBuf.writeLong(fileTransfer.getTotalFileSize());
                channel.writeAndFlush(firstMessageByteBuf);
            }
        }
    }

}
