package de.moldiy.molnet.exchange.massageexchanger;

import io.netty.channel.Channel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileTransfer {

    private final Channel channel;
    private final String path;
    private final File file;

    private final long fileTotalSize;

    private FileInputStream fileInputStream;

    public FileTransfer(Channel channel, String path, File file) {
        this.channel = channel;
        this.path = path;
        this.file = file;

        this.fileTotalSize = file.length();
    }

    public void open() throws FileNotFoundException {
        this.fileInputStream = new FileInputStream(this.file);
    }

    public boolean isOpen() {
        return this.fileInputStream != null;
    }

    public int read(byte[] b) throws IOException {
        return this.fileInputStream.read(b);
    }

    public void close() throws IOException {
        this.fileInputStream.close();
    }

    public long getTotalFileSize() {
        return fileTotalSize;
    }

    public File getFile() {
        return file;
    }

    public String getPath() {
        return path;
    }

    public Channel getChannel() {
        return channel;
    }
}
