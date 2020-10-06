package de.moldiy.molnet.server;

import de.moldiy.molnet.client.ClientChannelConnection;
import de.moldiy.molnet.exchange.MessageExchangerManager;
import de.moldiy.molnet.exchange.MessageHandler;
import de.moldiy.molnet.exchange.RightRestrictedMethodHandle;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class ServerMessageHandler extends MessageHandler {

    private Server server;

    @Override
    protected void execMethod(RightRestrictedMethodHandle methodHandle, BitVector rightBits, ChannelHandlerContext ctx,
                              ByteBuf byteBuf) {

        ServerChannelConnection clientChannelConnection = new ServerChannelConnection(ctx, this.server);

        try {
            methodHandle.invoke(rightBits, clientChannelConnection, byteBuf);
        } catch (RightRestrictedMethodHandle.NoAccessRightsException e) {
            this.handleNoAccessRight(ctx, rightBits);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    protected abstract void handleNoAccessRight(ChannelHandlerContext ctx, BitVector rightBits);

    void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}
