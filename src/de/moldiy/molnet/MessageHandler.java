package de.moldiy.molnet;

import de.moldiy.molnet.exchange.*;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.List;

@ChannelHandler.Sharable
public abstract class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private NetworkInterface networkInterface;

    private MessageExchangerManager messageExchangerManager;

    private MolNetMethodHandleInvoker molNetMethodHandleInvoker;

    void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        this.messageExchangerManager = new MessageExchangerManagerImpl(networkInterface.rightIDFactory);
        this.molNetMethodHandleInvoker = new MolNetMethodHandleInvoker(this.networkInterface);
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public abstract BitVector getRightBitsFromChannel(ChannelHandlerContext ctx);

    protected abstract void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id);

    protected abstract void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        List<MolNetMethodHandle> methodHandles = messageExchangerManager.getMethodsFromAnnotation(RunOnChannelConnect.class);
        if(methodHandles != null) {
            for (int i = methodHandles.size() - 1; i >= 0; i--) {
                MolNetMethodHandle methodHandle = methodHandles.get(i);
                try {
                    this.invokeRightRestrictedMethodHandle(methodHandle, ctx, null);
                } catch (MolNetMethodHandle.NoAccessRightsException e) {
                    this.handleNoAccessRight(this.messageExchangerManager.getIdMethods().getKey(methodHandle), ctx);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        String trafficID = NettyByteBufUtil.readUTF16String(byteBuf);

        MolNetMethodHandle methodHandle;
        try {
            methodHandle = this.messageExchangerManager.getRightRestrictedMethodHandle(trafficID);
        } catch (MessageExchangerManager.TrafficIDNotExists e) {
            this.handleTrafficIDNotFound(ctx, trafficID);
            return;
        }

        try {
            this.invokeRightRestrictedMethodHandle(methodHandle, ctx, byteBuf);
        } catch (MolNetMethodHandle.NoAccessRightsException e) {
            this.handleNoAccessRight(trafficID, ctx);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO catch channel disconnect
        if(cause instanceof IOException) {
            return;
        }
        super.exceptionCaught(ctx, cause);
    }

    private void invokeRightRestrictedMethodHandle(MolNetMethodHandle methodHandle, ChannelHandlerContext ctx, ByteBuf byteBuf) throws Throwable {
        this.molNetMethodHandleInvoker.invokeMethod(this.getRightBitsFromChannel(ctx), methodHandle, ctx, byteBuf);
    }

    public MessageExchangerManager getMessageExchangerManager() {
        return messageExchangerManager;
    }

    public void loadMessageExchanger(Object object) {
        this.messageExchangerManager.loadMessageExchanger(object);
    }

}
