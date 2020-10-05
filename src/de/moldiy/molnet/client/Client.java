package de.moldiy.molnet.client;

import de.moldiy.molnet.*;
import de.moldiy.molnet.exchange.MessageHandler;
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
public class Client {

    private final Client that = this;

    private final String host;
    private final int port;

    private final Bootstrap bootstrap;

    private Channel c;

    private final AuthenticateHandler authenticateHandler;

    public Client(String host, int port, AuthenticateHandler authenticateHandler) {
        this.host = host;
        this.port = port;
        this.authenticateHandler = authenticateHandler;

        this.bootstrap = new Bootstrap();
        this.bootstrap.group(new NioEventLoopGroup());
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addFirst("decoder", new MassageReader());
                ch.pipeline().addFirst("encoder", new MassageWriter());

                ch.pipeline().addLast("auth", authenticateHandler);

                ch.pipeline().addLast("handler", new MessageHandler());
            }

            ;
        });
    }

    public Client(String host, int port) {
        this(host, port, new AuthenticateHandler());
    }

    public void connect() throws InterruptedException {
        this.c = this.bootstrap.connect(this.host, this.port).sync().channel();
    }

    public void connectAndAuth(String... args) throws InterruptedException {
        this.connect();
        this.authenticateHandler.authenticate(args);
    }

    public void authenticate(String... args) {
        this.authenticateHandler.authenticate(args);
    }

    public void loadMessageExchanger(Object o) {

    }

    public void write(String trafficID, ByteBuf byteBuf) {
        c.write(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public void writeAndFlush(String trafficID, ByteBuf byteBuf) {
        this.write(trafficID, byteBuf);
        c.flush();
    }

    public void flush() {
        c.flush();
    }

    public Channel getChannel() {
        return c;
    }

}
