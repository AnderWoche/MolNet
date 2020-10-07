package de.moldiy.molnet.client;

import de.moldiy.molnet.MassageReader;
import de.moldiy.molnet.MassageWriter;
import de.moldiy.molnet.MessageWriter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author David Humann (Moldiy)
 */
public abstract class Client extends ClientMessageHandler implements MessageWriter {

    private final Client that = this;

    private final String host;
    private final int port;

    private final Bootstrap bootstrap;

    private Channel c;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;

        super.setClient(this);

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(new NioEventLoopGroup());
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addFirst("decoder", new MassageReader());
                ch.pipeline().addFirst("encoder", new MassageWriter());

                ch.pipeline().addLast("handler", that);
            }
        });
    }

    public void connect() throws InterruptedException {
        this.c = this.bootstrap.connect(this.host, this.port).sync().channel();
    }

    @Override
    public void broadCastMassage(String trafficID, ByteBuf byteBuf) {
        this.writeAndFlush(c, trafficID, byteBuf);
    }

    public Channel getChannel() {
        return c;
    }
}
