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

    void setNetworkInterface(NetworkInterface networkInterface) {
        this.networkInterface = networkInterface;
        this.messageExchangerManager = new MessageExchangerManagerImpl(networkInterface.rightIDFactory);
    }

    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }

    public abstract BitVector getRightBitsFromChannel(ChannelHandlerContext ctx);

    protected abstract void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id);

    protected abstract void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        List<RightRestrictedMethodHandle> methodHandles = messageExchangerManager.getMethodsFromAnnotation(RunOnChannelConnect.class);
        if(methodHandles != null) {
            for (int i = methodHandles.size() - 1; i >= 0; i--) {
                RightRestrictedMethodHandle methodHandle = methodHandles.get(i);
                try {
                    this.invokeRightRestrictedMethodHandle(methodHandle, ctx, null);
                } catch (RightRestrictedMethodHandle.NoAccessRightsException e) {
                    this.handleNoAccessRight(this.messageExchangerManager.getIdMethods().getKey(methodHandle), ctx);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws IOException {
        String trafficID = NettyByteBufUtil.readUTF16String(byteBuf);

        RightRestrictedMethodHandle methodHandle;
        try {
            methodHandle = this.messageExchangerManager.getRightRestrictedMethodHandle(trafficID);
        } catch (MessageExchangerManager.TrafficIDNotExists e) {
            this.handleTrafficIDNotFound(ctx, trafficID);
            return;
        }

        try {
            this.invokeRightRestrictedMethodHandle(methodHandle, ctx, byteBuf);
        } catch (RightRestrictedMethodHandle.NoAccessRightsException e) {
            this.handleNoAccessRight(trafficID, ctx);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void invokeRightRestrictedMethodHandle(RightRestrictedMethodHandle methodHandle, ChannelHandlerContext ctx, ByteBuf byteBuf) throws Throwable {
        BitVector rightBits = this.getRightBitsFromChannel(ctx);
        methodHandle.invoke(rightBits, this.networkInterface, ctx, byteBuf);
    }

    public MessageExchangerManager getMessageExchangerManager() {
        return messageExchangerManager;
    }

    public void loadMessageExchanger(Object object) {
        this.messageExchangerManager.loadMassageExchanger(object);
    }

}
