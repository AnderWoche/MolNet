package de.moldiy.molnet.exchange;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public abstract class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final RightIDFactory rightIDFactory = new RightIDFactory(true);

    private final MessageExchangerManager messageExchangerManager;

    public MessageHandler() {
        this.messageExchangerManager = new MessageExchangerManager(this.rightIDFactory);
    }

    public abstract BitVector getRightBitsFromChannel(ChannelHandlerContext ctx);

    protected abstract void execMethod(RightRestrictedMethodHandle methodHandle, BitVector rightBits, ChannelHandlerContext ctx, ByteBuf byteBuf);

    protected abstract void handleTrafficIDNotFound(String id);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        String id = NettyByteBufUtil.readUTF16String(byteBuf);

        System.out.println(ctx.channel());

        RightRestrictedMethodHandle methodHandle;
        try {
            methodHandle = this.messageExchangerManager.getRightRestrictedMethodHandle(id);
        } catch (MessageExchangerManager.TrafficIDNotExists e) {
            this.handleTrafficIDNotFound(id);
            return;
        }

        BitVector rightBits = this.getRightBitsFromChannel(ctx);

        this.execMethod(methodHandle, rightBits, ctx, byteBuf);
    }

    public RightIDFactory getRightIDFactory() {
        return rightIDFactory;
    }

    public MessageExchangerManager getMessageExchangerManager() {
        return messageExchangerManager;
    }

    public void loadMessageExchanger(Object object) {
        this.messageExchangerManager.loadMassageExchanger(object);
    }
}
