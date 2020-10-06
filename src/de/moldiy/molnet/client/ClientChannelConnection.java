package de.moldiy.molnet.client;

import de.moldiy.molnet.client.Client;
import de.moldiy.molnet.exchange.MolChannelConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ClientChannelConnection extends MolChannelConnection {

    private final Client client;

    public ClientChannelConnection(ChannelHandlerContext ctx, Client client) {
        super(ctx);
        this.client = client;
    }

    public void write(String trafficID, ByteBuf byteBuf) {
        this.client.write(trafficID, byteBuf);
    }

    public void writeAndFlush(String trafficID, ByteBuf byteBuf) {
        this.client.writeAndFlush(trafficID, byteBuf);
    }

    public void flush() {
        this.client.flush();
    }

    public Client getClient() {
        return client;
    }
}
