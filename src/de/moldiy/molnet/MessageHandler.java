package de.moldiy.molnet;

import de.moldiy.molnet.MessageWriter;
import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.MessageExchangerManager;
import de.moldiy.molnet.exchange.RightIDFactory;
import de.moldiy.molnet.exchange.RightRestrictedMethodHandle;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public abstract class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private NetworkInterface networkInterface;

    private MessageExchangerManager messageExchangerManager;

    void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        this.messageExchangerManager = new MessageExchangerManager(networkInterface.rightIDFactory);
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public abstract BitVector getRightBitsFromChannel(ChannelHandlerContext ctx);

    protected abstract void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id);

    protected abstract void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx, BitVector rightBits);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        String trafficID = NettyByteBufUtil.readUTF16String(byteBuf);

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
