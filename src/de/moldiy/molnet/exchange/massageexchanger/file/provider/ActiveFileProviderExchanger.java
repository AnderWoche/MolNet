package de.moldiy.molnet.exchange.massageexchanger.file.provider;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.Threaded;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.exchange.massageexchanger.file.FileExchangerConstants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class ActiveFileProviderExchanger {

    private final HashMap<String, FilePacket> providedFiles = new HashMap<>();

    public void provide(String name, Path path) throws IOException {
        if (name == null) throw new NullPointerException("The name can't be null");
        this.providedFiles.put(name, new FilePacket(path));
    }

    @Threaded
    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_PROVIDER_REQUEST)
    private void net_fileRequest(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf message) throws IOException {
        String name = NettyByteBufUtil.readUTF16String(message);
        FilePacket filePacket = this.providedFiles.get(name);

        if(filePacket == null) {
            networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_DO_NOT_EXISTS);
            return;
        }

        ByteBuf filePacketBuffer = ctx.alloc().buffer();
        filePacketBuffer.writeInt(filePacket.getFiles().size());
        filePacketBuffer.writeLong(filePacket.getTotalTransferSize());
        networkInterface.write(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_PACKET, filePacketBuffer);

        List<File> filesToSend = filePacket.getFiles();
        List<String> filePathsToSend = filePacket.getRelativeFilePath();

        for(int i = filesToSend.size() - 1; i >= 0; i--) {
            File file = filesToSend.get(i);
            String relativePath = filePathsToSend.get(i);
            long fileSize = file.length();

            ByteBuf fileMessageByteBuf = ctx.alloc().buffer();
            NettyByteBufUtil.writeUTF16String(fileMessageByteBuf, relativePath);
            fileMessageByteBuf.writeLong(fileSize);
            networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_NEW_FILE, fileMessageByteBuf);

            FileInputStream fileInputStream = new FileInputStream(file);

            byte[] read = new byte[FileExchangerConstants.SAVED_FILE_BYTES_IN_RAM];
            int readSize;
            while (((readSize = fileInputStream.read(read)) != -1)) {
                ByteBuf byteBuf = ctx.alloc().buffer();
                byteBuf.writeBytes(read, 0, readSize);

                while (!ctx.channel().isWritable()) {
                    Thread.yield();
                }

                networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_WRITE, byteBuf);
            }
        }
        networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_DONE);
    }
}
