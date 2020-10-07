package de.moldiy.molnet.client;

import de.moldiy.molnet.exchange.MessageExchangerManager;
import de.moldiy.molnet.exchange.MessageHandler;
import de.moldiy.molnet.exchange.RightRestrictedMethodHandle;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class ClientMessageHandler extends MessageHandler {

    private Client client;

    @Override
    protected void execMethod(RightRestrictedMethodHandle methodHandle, BitVector rightBits, ChannelHandlerContext ctx, ByteBuf byteBuf) {

        try {
            methodHandle.invoke(rightBits, clientChannelConnection, byteBuf);
        } catch(RightRestrictedMethodHandle.NoAccessRightsException e) {
            this.handleNoAccessRight(ctx, rightBits);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    protected abstract void handleNoAccessRight(ChannelHandlerContext ctx, BitVector rightBits);

    void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return this.client;
    }
}
