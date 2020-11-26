package de.moldiy.molnet.exchange;

import io.netty.buffer.ByteBuf;


public interface DTOSerializer {

    ByteBuf serialize(ByteBuf byteBuf);

    void deserialize(ByteBuf byteBuf);

}
