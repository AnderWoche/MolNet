package de.moldiy.molnet.exchange.massageexchanger.file.passive;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.exchange.massageexchanger.file.FileExchangerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassiveFileReceiverExchanger {

    private final List<FileExchangerListener> listeners = new ArrayList<>();

    private long totalFileSize = 0;
    private long currentFileSize = 0;
    private String path;
    private FileOutputStream fileOutputStream = null;

    @TrafficID(id = FileExchangerConstants.PASSIVE_FILE_RECEIVER_NEW)
    public void net_newFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        if (this.fileOutputStream != null) {
            this.fileOutputStream.flush();
            this.fileOutputStream.close();
        }

        this.path = NettyByteBufUtil.readUTF16String(byteBuf);
        this.totalFileSize = byteBuf.readLong();
        this.currentFileSize = 0;
        try {
            this.fileOutputStream = new FileOutputStream(this.path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.notifyCreateNewFileListener(this.path, this.totalFileSize);

        networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.PASSIVE_FILE_SENDER_REQUEST);
    }

    @TrafficID(id = FileExchangerConstants.PASSIVE_FILE_RECEIVER_WRITE)
    private void net_receiveFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        if(this.fileOutputStream != null) {
            byte[] readBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(readBytes);

            this.currentFileSize += readBytes.length;

            this.fileOutputStream.write(readBytes);

            this.notifyReceiveFileListener(this.currentFileSize, this.totalFileSize);

            if ((this.totalFileSize - this.currentFileSize) == 0) {
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
                this.notifyFileSuccessfullyReceived(this.path, this.totalFileSize);
            } else {
                networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.PASSIVE_FILE_SENDER_REQUEST);
            }
        }
    }

    public void addListener(FileExchangerListener fileMassageExchangerListener) {
        this.listeners.add(fileMassageExchangerListener);
    }

    public void removeListener(FileExchangerListener fileMassageExchangerListener) {
        this.listeners.remove(fileMassageExchangerListener);
    }

    public void notifyCreateNewFileListener(String path, long totalFileSize) {
        for(int i = this.listeners.size() - 1; i >= 0; i--) {
            this.listeners.get(i).createNewFile(path, totalFileSize);
        }
    }

    public void notifyReceiveFileListener(long currentFileSize, long totalFileSize) {
        for(int i = this.listeners.size() - 1; i >= 0; i--) {
            this.listeners.get(i).receiveFile(currentFileSize, totalFileSize);
        }
    }

    public void notifyFileSuccessfullyReceived(String path, long totalFileSize) {
        for(int i = this.listeners.size() - 1; i >= 0; i--) {
            this.listeners.get(i).fileSuccessfullyReceived(path, totalFileSize);
        }
    }

}
