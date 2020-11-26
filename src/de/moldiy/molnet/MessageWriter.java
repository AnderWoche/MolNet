package de.moldiy.molnet;

import com.badlogic.gdx.utils.Pools;
import de.moldiy.molnet.exchange.DTOSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

public interface MessageWriter {

    default void write(Channel channel, String trafficID, ByteBuf message) {
        channel.write(NettyByteBufUtil.addStringBeforeMassage(trafficID, message));
    }

    default void write(Channel channel, String trafficID) {
        channel.write(NettyByteBufUtil.addStringBeforeMassage(trafficID, channel.alloc().buffer()));
    }

    default void writeAndFlush(Channel channel, String trafficID, ByteBuf message) {
        this.write(channel, trafficID, message);
        channel.flush();
    }

    default void writeAndFlush(Channel channel, String trafficID) {
        this.writeAndFlush(channel, trafficID, channel.alloc().buffer());
    }

    default <T extends DTOSerializer> void writeAndFlush(Channel channel, String trafficID, T message) {
        this.writeAndFlush(channel, trafficID, message.serialize(channel.alloc().buffer()));
    }

    default <T extends DTOSerializer> void writeAndFlushAndFreeObject(Channel channel, String trafficID, T message) {
        this.writeAndFlush(channel, trafficID, message.serialize(channel.alloc().buffer()));
        Pools.free(message);
    }

    default <T extends DTOSerializer> T obtainDTO(Class<T> DTOClass) {
        return Pools.obtain(DTOClass);
    }

    default void flush(Channel channel) {
        channel.flush();
    }
}
