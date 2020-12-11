package de.moldiy.molnet;

import de.moldiy.molnet.exchange.DTOSerializer;
import de.moldiy.molnet.utils.gdx.Pools;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.FileNotFoundException;

public interface NetworkInterfaceMessageWriter extends MessageWriter {

    void broadcastMassage(String trafficID, ByteBuf byteBuf);

    void writeFile(Channel channel, String path, String file) throws FileNotFoundException;

    default <T extends DTOSerializer> T obtainDTO(Class<T> DTOClass) {
        return Pools.obtain(DTOClass);
    }

    default <T extends DTOSerializer> void writeAndFlushDTO(Channel channel, String trafficID, T dto) {
        writeAndFlush(channel, trafficID, dto.serialize(channel.alloc().buffer()));
        Pools.free(dto);
    }

    <T extends DTOSerializer> void writeAndFlushDTO(String identifierName, T value, String trafficID, T dto);

    <T extends DTOSerializer> void broadcastDTO(String trafficID, T dto);

}
