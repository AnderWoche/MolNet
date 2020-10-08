package de.moldiy.molnet;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author David Humann (Moldiy)
 */
public class Client extends NetworkInterface {

    private final String host;
    private final int port;

    private final Bootstrap bootstrap;

    private final MessageHandler messageHandler;

    private Channel c;

    public Client(String host, int port, MessageHandler messageHandler) {
        this.host = host;
        this.port = port;
        this.messageHandler = messageHandler;

        messageHandler.setNetworkInterface(this);

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(new NioEventLoopGroup());
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addFirst("decoder", new MessageDecoder());
                ch.pipeline().addFirst("encoder", new MessageEncoder());

                ch.pipeline().addLast("handler", messageHandler);
            }
        });
    }

    public ChannelFuture connect() {
        ChannelFuture channelFuture = this.bootstrap.connect(this.host, this.port);
        this.c = channelFuture.channel();
        return channelFuture;
    }

    @Override
    public void broadCastMassage(String trafficID, ByteBuf byteBuf) {
        this.writeAndFlush(c, trafficID, byteBuf);
    }

    public Channel getChannel() {
        return c;
    }

    public void loadMessageExchanger(Object object) {
        this.messageHandler.loadMessageExchanger(object);
    }
}
