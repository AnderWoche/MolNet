package de.moldiy.molnet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.FileNotFoundException;

public interface AdvancedMessageWriter extends MessageWriter {

    void broadCastMassage(String trafficID, ByteBuf byteBuf);

    void writeFile(Channel channel, String path, String file) throws FileNotFoundException;

}
