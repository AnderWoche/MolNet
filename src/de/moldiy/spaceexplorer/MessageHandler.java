package de.moldiy.spaceexplorer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private final MessageExchangerManager messageExchangerManager;

    public MessageHandler(MessageExchangerManager exchangerManager) {
        this.messageExchangerManager = exchangerManager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
        String id = NettyByteBufUtil.readUTF16String(byteBuf);
        this.messageExchangerManager.exec(id, channelHandlerContext, byteBuf);
    }
}
