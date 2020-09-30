package de.moldiy.molnet.server;

import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public abstract class ServerMessageExchanger {

    private Server s;

    public void broadCastMassage(String trafficID, ByteBuf byteBuf) {
        this.s.getAllClients().writeAndFlush(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public void write(ChannelHandlerContext ctx, String trafficID, ByteBuf byteBuf) {
        ctx.channel().write(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public void writeAndFlush(ChannelHandlerContext ctx, String trafficID, ByteBuf byteBuf) {
        this.write(ctx, trafficID, byteBuf);
        ctx.flush();
    }

    public Server getServer() {
        return s;
    }
}
