package de.moldiy.molnet.client;

import de.moldiy.molnet.NettyByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ClientMassageExchanger {

    private Client c;

    public void write(String trafficID, ByteBuf byteBuf) {
        c.write(trafficID, byteBuf);
    }

    public void writeAndFlush(String trafficID, ByteBuf byteBuf) {
        this.writeAndFlush(trafficID, byteBuf);
    }

    public void flush() {
        c.flush();
    }

    public Client getClient() {
        return c;
    }
}
