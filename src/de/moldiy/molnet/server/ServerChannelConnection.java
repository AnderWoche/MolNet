package de.moldiy.molnet.server;

import de.moldiy.molnet.exchange.MolChannelConnection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ServerChannelConnection extends MolChannelConnection {

    private final Server server;

    public ServerChannelConnection(ChannelHandlerContext ctx, Server server) {
        super(ctx);
        this.server = server;
    }

    public void broadCastMassage(String trafficID, ByteBuf byteBuf) {
        this.server.broadCastMassage(trafficID, byteBuf);
    }

    public void reply(String trafficID, ByteBuf byteBuf) {
        this.server.write(super.ctx, trafficID, byteBuf);
    }

    public void replyAndFlush(String trafficID, ByteBuf byteBuf) {
        this.server.writeAndFlush(super.ctx, trafficID, byteBuf);
    }

    public void write(ChannelHandlerContext ctx, String trafficID, ByteBuf byteBuf) {
        this.server.write(ctx, trafficID, byteBuf);
    }

    public void writeAndFlush(ChannelHandlerContext ctx, String trafficID, ByteBuf byteBuf) {
        this.server.writeAndFlush(ctx, trafficID, byteBuf);
    }

    public void flush(ChannelHandlerContext ctx) {
        this.server.flush(ctx);
    }

    public Server getServer() {
        return this.server;
    }

}
