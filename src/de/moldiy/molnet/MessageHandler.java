package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final int FAILED_MESSAGE_COUNT_FOR_DISCONNECT = 5;

    private int failedMessageCount;

    private final MessageExchangerManager messageExchangerManager;

    public MessageHandler(MessageExchangerManager exchangerManager) {
        this.messageExchangerManager = exchangerManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        String id = NettyByteBufUtil.readUTF16String(byteBuf);
        boolean success = this.messageExchangerManager.exec(id, channelHandlerContext, byteBuf);
        if(success) {
            this.failedMessageCount = 0;
        } else {
            if(this.failedMessageCount >= FAILED_MESSAGE_COUNT_FOR_DISCONNECT) {
                channelHandlerContext.disconnect();
            }
        }
    }
}
