package de.moldiy.molnet.client;

import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ClientMassageExchanger {

    private Client c;

    public void write(String trafficID, ByteBuf byteBuf) {
        c.getChannel().write(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public void writeAndFlush(String trafficID, ByteBuf byteBuf) {
        this.write(trafficID, byteBuf);
        c.getChannel().flush();
    }

    public void flush() {
        c.flush();
    }

    public Client getClient() {
        return c;
    }
}
