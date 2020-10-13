package de.moldiy.molnet;

import de.moldiy.molnet.MessageWriter;
import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.MessageExchangerManager;
import de.moldiy.molnet.exchange.MessageExchangerManagerImpl;
import de.moldiy.molnet.exchange.RightIDFactory;
import de.moldiy.molnet.exchange.RightRestrictedMethodHandle;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@ChannelHandler.Sharable
public abstract class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private NetworkInterface networkInterface;

    private MessageExchangerManager messageExchangerManager;

    void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        this.messageExchangerManager = new MessageExchangerManagerImpl(networkInterface.rightIDFactory);
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public abstract BitVector getRightBitsFromChannel(ChannelHandlerContext ctx);

    protected abstract void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id);

    protected abstract void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx, BitVector rightBits);


    boolean isFileReading = false;
    long fileSize = 0;
    int currentSize = 0;
    String path;
    FileOutputStream fileOutputStream = null;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        String trafficID = NettyByteBufUtil.readUTF16String(byteBuf);

        if(trafficID.equals("molnet.file")) {
            if(!isFileReading && fileOutputStream == null) {
                this.isFileReading = true;
                this.path = NettyByteBufUtil.readUTF16String(byteBuf);
                System.out.println("FILE PATH: " + this.path);
                this.fileSize = byteBuf.readLong();
                System.out.println("FILE SIZE: " + fileSize);
                this.fileOutputStream = new FileOutputStream(this.path);
                this.fileOutputStream.flush();
                return;
            }

            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);

            currentSize += bytes.length;
            System.out.println(currentSize);

            fileOutputStream.write(bytes);

            if((fileSize - currentSize) == 0) {
                this.isFileReading = false;
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        RightRestrictedMethodHandle methodHandle;
        try {
            methodHandle = this.messageExchangerManager.getRightRestrictedMethodHandle(trafficID);
        } catch (MessageExchangerManager.TrafficIDNotExists e) {
            this.handleTrafficIDNotFound(ctx, trafficID);
            return;
        }

        BitVector rightBits = this.getRightBitsFromChannel(ctx);

        try {
            methodHandle.invoke(rightBits, this.networkInterface, ctx, byteBuf);
        } catch (RightRestrictedMethodHandle.NoAccessRightsException e) {
            this.handleNoAccessRight(trafficID, ctx, rightBits);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public MessageExchangerManager getMessageExchangerManager() {
        return messageExchangerManager;
    }

    public void loadMessageExchanger(Object object) {
        this.messageExchangerManager.loadMassageExchanger(object);
    }

}
