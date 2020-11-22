package de.moldiy.molnet.exchange.massageexchanger.file.provider;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileDownloadProcessor {

    private final List<FileDownloadListener> fileDownloadListeners = new ArrayList<>();

    private final NetworkInterface networkInterface;
    private final Channel channel;

    private final String name;
    private final Path directory;

    private Integer filesToDownloadAmount = null;
    private Long bytesToDownload = null;

    private long bytesRead = 0;

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

    void setFilesToDownloadAmount(Integer filesToDownloadAmount) {
        this.filesToDownloadAmount = filesToDownloadAmount;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    long addBytesRead(long amount) {
        return this.bytesRead += amount;
    }

    public Integer getFilesToDownloadAmount() {
        return filesToDownloadAmount;
    }

    void setBytesToDownload(Long bytesToDownload) {
        this.bytesToDownload = bytesToDownload;
    }

    public Long getBytesToDownload() {
        return bytesToDownload;
    }

    public void addListener(FileDownloadListener fileDownloadListener) {
        this.fileDownloadListeners.add(fileDownloadListener);
    }

    public void removeListener(FileDownloadListener fileDownloadListener) {
        this.fileDownloadListeners.remove(fileDownloadListener);
    }

    synchronized void notifyNewFileListener(File file) {
        for(FileDownloadListener listener : this.fileDownloadListeners) {
            listener.newFile(file);
        }
    }

    synchronized void notifyBytesReceived() {
        for(FileDownloadListener listener : this.fileDownloadListeners) {
            listener.bytesReceived(this.bytesRead, this.bytesToDownload);
        }
    }

    synchronized void notifyDone(FileDownloadFuture fileDownloadFuture) {
        for(FileDownloadListener listener : this.fileDownloadListeners) {
            listener.done(fileDownloadFuture);
        }
        this.notifyAll();
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

    public synchronized void sync() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
