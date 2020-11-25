package de.moldiy.molnet.exchange.massageexchanger.file.provider;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.exchange.massageexchanger.file.FileExchangerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ActiveFileReaderExchanger {

    private final List<FileDownloadProcessor> processors = new ArrayList<>();
    private FileDownloadProcessor activeProcessor;

    private FileOutputStream fileOutputStream;

    public FileDownloadProcessor requestFile(NetworkInterface networkInterface, Channel channel, String name, Path directory) {
        FileDownloadProcessor fileDownloadProcessor = new FileDownloadProcessor(networkInterface, channel, name, directory);
        this.processors.add(fileDownloadProcessor);
        this.requestNewProcessor();
        return fileDownloadProcessor;
    }

    private void requestNewProcessor() {
        if (this.activeProcessor == null && this.processors.size() > 0) {
            this.activeProcessor = this.processors.get(0);

            ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
            NettyByteBufUtil.writeUTF16String(byteBuf, this.activeProcessor.getName());
            this.activeProcessor.writeAndFlush(FileExchangerConstants.ACTIVE_FILE_PACKET_PROVIDER_REQUEST, byteBuf);
        }
    }

    private void removeOldProcessor() {
        if (this.activeProcessor != null) {
            this.processors.remove(this.activeProcessor);
            this.activeProcessor = null;
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_PACKET)
    public void net_newFilePacket(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (this.activeProcessor != null) {
            this.activeProcessor.setFilesToDownloadAmount(byteBuf.readInt());
            this.activeProcessor.setBytesToDownload(byteBuf.readLong());
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_FILE)
    public void net_newFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (this.activeProcessor != null) {
            try {
                File file = this.getFileLocation(NettyByteBufUtil.readUTF16String(byteBuf));
                this.closeFileInputStream();
                this.fileOutputStream = new FileOutputStream(file);
                this.activeProcessor.notifyNewFileListener(file);
            } catch(FileNotFoundException e) {
                System.err.println(e.getMessage());
                this.fileOutputStream = null;
            } catch (IOException e) {
                this.fileDownloadDoneWithException(e);
            }
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_WRITE)
    private void net_writeFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (this.activeProcessor != null && this.fileOutputStream != null) {
            byte[] readBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(readBytes);

            this.activeProcessor.addBytesRead(readBytes.length);
            this.activeProcessor.notifyBytesReceived();

            try {
                this.fileOutputStream.write(readBytes);
            } catch (IOException e) {
                this.fileDownloadDoneWithException(e);
            }
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_DONE)
    public void net_done(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        if (this.activeProcessor != null && this.fileOutputStream != null) {
            try {
                this.closeFileInputStream();
                this.fileDownloadSuccessfullyDone();
            } catch (IOException e) {
                this.fileDownloadDoneWithException(e);
            }
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_DO_NOT_EXISTS)
    private void net_FilePacketNameNotExists(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        this.fileDownloadDoneWithException(new PacketNameDoNotExists("The " + this.activeProcessor.getName() + " FilePacket name do not exists on the Server."));
    }

    private File getFileLocation(String path) throws IOException {
        Path p = Paths.get(this.activeProcessor.getDirectory().toString(), path);

        Path folders = p.resolveSibling("");
        Files.createDirectories(folders);

        return p.toFile();
    }

    private void fileDownloadSuccessfullyDone() {
        this.activeProcessor.notifyDone(new FileDownloadFuture(true, null));
        this.removeOldProcessor();
        this.requestNewProcessor();
    }

    private void fileDownloadDoneWithException(Throwable throwable) {
        this.activeProcessor.notifyDone(new FileDownloadFuture(false, throwable));
        this.removeOldProcessor();
        this.requestNewProcessor();
    }

    private void closeFileInputStream() throws IOException {
        if (this.fileOutputStream != null) {
            this.fileOutputStream.close();
            this.fileOutputStream = null;
        }
    }

    public static class PacketNameDoNotExists extends RuntimeException {
        public PacketNameDoNotExists(String message) {
            super(message);
        }
    }
}
