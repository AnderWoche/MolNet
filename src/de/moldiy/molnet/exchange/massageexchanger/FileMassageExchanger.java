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

public class FileMassageExchanger {

    private final List<FileMassageExchangerListener> listeners = new ArrayList<>();

    private long totalFileSize = 0;
    private long currentFileSize = 0;
    private FileOutputStream fileOutputStream = null;

    @TrafficID(id = "molnet.file.new")
    public void newFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        if (this.fileOutputStream != null) {
            this.fileOutputStream.flush();
            this.fileOutputStream.close();
        }

        String path = NettyByteBufUtil.readUTF16String(byteBuf);
        this.totalFileSize = byteBuf.readLong();
        try {
            this.fileOutputStream = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        this.notifyCreateNewFileListener(path, this.totalFileSize);
    }

    @TrafficID(id = "molnet.file.write")
    public void receiveFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        if(this.fileOutputStream != null) {
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            this.currentFileSize += bytes.length;

            this.notifyReceiveFileListener(this.currentFileSize, this.totalFileSize);

            this.fileOutputStream.write(bytes);

            if ((this.totalFileSize - this.currentFileSize) == 0) {
                this.fileOutputStream.flush();
                this.fileOutputStream.close();
            }
        }
    }

    public void addListener(FileMassageExchangerListener fileMassageExchangerListener) {
        this.listeners.add(fileMassageExchangerListener);
    }

    public void removeListener(FileMassageExchangerListener fileMassageExchangerListener) {
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

}
