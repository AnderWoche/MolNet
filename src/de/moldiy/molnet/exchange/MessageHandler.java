package de.moldiy.molnet.exchange;

import de.moldiy.molnet.NettyByteBufUtil;
import de.moldiy.molnet.exchange.MessageExchangerManager;
import de.moldiy.molnet.server.ConnectionIdentifierManager;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class MessageHandler extends SimpleChannelInboundHandler<ByteBuf> {

    public static final int FAILED_MESSAGE_COUNT_FOR_DISCONNECT = 5;

    private int failedMessageCount;

    private final RightIDFactory rightIDFactory = new RightIDFactory(true);

    private final ConnectionIdentifierManager connectionIdentifierManager = new ConnectionIdentifierManager();
    private final MessageExchangerManager messageExchangerManager;

    public MessageHandler() {
        this.messageExchangerManager = new MessageExchangerManager(this.rightIDFactory);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) {
        String id = NettyByteBufUtil.readUTF16String(byteBuf);

        BitVector bitVector = connectionIdentifierManager.getIdentifierFromConnection("right", ctx);

        boolean success = false;
        try {
            success = this.messageExchangerManager.exec(id, bitVector, ctx, byteBuf);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        if (success) {
            this.failedMessageCount = 0;
        } else {
            if (this.failedMessageCount >= FAILED_MESSAGE_COUNT_FOR_DISCONNECT) {
                ctx.disconnect();
            }
        }
    }

    public RightIDFactory getRightIDFactory() {
        return rightIDFactory;
    }


}
