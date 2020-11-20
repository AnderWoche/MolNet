package de.moldiy.molnet.exchange.massageexchanger.file.active;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.exchange.massageexchanger.file.FileExchangerConstants;
import de.moldiy.molnet.utils.MapArray;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class ActiveFileProviderExchanger {

    private final HashMap<String, FilePacket> providedFiles = new HashMap<>();

    private final MapArray<Channel, FilePacketReader> packetsToSend = new MapArray<>();

    public void provide(String name, Path path) throws IOException {
        if(name == null) throw new NullPointerException("The name can't be null");
        this.providedFiles.put(name, new FilePacket(path));
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_PROVIDER_GET_FILE_PACKED)
    private void net_getFilePacked(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        String packetName = NettyByteBufUtil.readUTF16String(byteBuf);
        FilePacket filePacket = this.providedFiles.get(packetName);
        if(filePacket != null) {
            ByteBuf sendBuffer = ctx.alloc().buffer();
            long totalSize = filePacket.getTotalTransferSize();
            sendBuffer.writeLong(totalSize);
            int filesAmount = filePacket.getFiles().size();
            sendBuffer.writeInt(filesAmount);
            for(File f : filePacket.getFiles()) {
                NettyByteBufUtil.writeUTF16String(sendBuffer, f.getName());
                System.out.println(f.getPath());
            }
        } else {
            networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_DO_NOT_EXISTS);
        }
    }

    @TrafficID(id = FileExchangerConstants.ACTIVE_FILE_PACKET_PROVIDER_REQUEST)
    private void net_sendFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf message) throws IOException {

        List<FilePacketReader> packetReaders = this.packetsToSend.get(ctx.channel());

        FilePacketReader filePacketReader = packetReaders.get(0);
        if(!filePacketReader.hasCurrentFile()) {
            filePacketReader.nextFile();
        }

        byte[] read = new byte[FileExchangerConstants.SAVED_FILE_BYTES_IN_RAM];
        int readSize = filePacketReader.read(read);

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(read, 0, readSize);

        networkInterface.writeAndFlush(ctx.channel(), FileExchangerConstants.ACTIVE_FILE_PACKET_WRITE, byteBuf);

        if (readSize < read.length) {
            filePacketReader.close();
            packetReaders.remove(0);
//            this.sendNewFile(ctx.channel());
        }
    }

//    private void sendNewFile(Channel channel) throws FileNotFoundException {
//        List<FilePassiveTransfer> transferList = this.filesToSend.get(channel);
//        if(!transferList.isEmpty()) {
//            FilePassiveTransfer fileTransfer = transferList.get(0);
//            if (fileTransfer != null && !fileTransfer.isOpen()) {
//                fileTransfer.open();
//
//                ByteBuf firstMessageByteBuf = channel.alloc().buffer();
//                NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, FileExchangerConstants.PASSIVE_FILE_RECEIVER_NEW);
//                NettyByteBufUtil.writeUTF16String(firstMessageByteBuf, fileTransfer.getPath());
//                firstMessageByteBuf.writeLong(fileTransfer.getTotalFileSize());
//                channel.writeAndFlush(firstMessageByteBuf);
//            }
//        }
//    }

}
