package de.moldiy.molnet.exchange.massageexchanger;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileMessageReceiverExchanger {

    private final List<FileMessageExchangerListener> listeners = new ArrayList<>();

    private long totalFileSize = 0;
    private long currentFileSize = 0;
    private String path;
    private FileOutputStream fileOutputStream = null;

    @TrafficID(id = "molnet.file.new")
    public void newFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
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

        networkInterface.writeAndFlush(ctx.channel(), "molnet.file.request");
    }

    @TrafficID(id = "molnet.file.write")
    public void receiveFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
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
                networkInterface.writeAndFlush(ctx.channel(), "molnet.file.request");
            }
        }
    }

    public void addListener(FileMessageExchangerListener fileMassageExchangerListener) {
        this.listeners.add(fileMassageExchangerListener);
    }

    public void removeListener(FileMessageExchangerListener fileMassageExchangerListener) {
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
