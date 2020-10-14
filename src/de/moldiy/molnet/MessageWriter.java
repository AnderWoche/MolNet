package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public interface MessageWriter {

    void broadCastMassage(String trafficID, ByteBuf byteBuf);

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

    default void flush(Channel channel) {
        channel.flush();
    }
}
