package de.moldiy.molnet;

import de.moldiy.molnet.exchange.DTOSerializer;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.ProviderFileReaderExchanger;
import de.moldiy.molnet.exchange.massageexchanger.file.passive.PassiveFileReceiverExchanger;
import de.moldiy.molnet.exchange.massageexchanger.file.passive.PassiveFileSenderExchanger;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;

/**
 * @author David Humann (Moldiy)
 */
public class Client extends NetworkInterface {

    private String host;
    private int port;

    private final Bootstrap bootstrap;

    private Channel c;

    public Client() {
        this(null, Integer.MIN_VALUE, new DefaultMessageHandler());
    }

    public Client(String host, int port) {
        this(host, port, new DefaultMessageHandler());
    }

    public Client(String host, int port, MessageHandler messageHandler) {
        super(messageHandler);
        this.host = host;
        this.port = port;

        messageHandler.setNetworkInterface(this);
        messageHandler.getMessageExchangerManager().setClientFilter();
        messageHandler.loadMessageExchanger(new PassiveFileReceiverExchanger());
        messageHandler.loadMessageExchanger(new ProviderFileReaderExchanger());

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

    /**
     * changes the host and the port on this client and Connect
     * @param host changes the host on this client and Connect
     * @param port changes the port on this client and Connect
     * @return The ChannelFuture
     */
    public ChannelFuture connect(String host, int port) {
        this.host = host;
        this.port = port;
        return this.connect();
    }

    public ChannelFuture connect() {
        if(this.host == null || this.port == Integer.MIN_VALUE) {
            throw new AddressNotSetRuntimeException("the Connection Address ist not set set it By the Connect Method or in the Constructor");
        }
        ChannelFuture channelFuture = this.bootstrap.connect(this.host, this.port);
        this.c = channelFuture.channel();
        return channelFuture;
    }

    @Override
    public void broadcastMassage(String trafficID, ByteBuf byteBuf) {
        this.writeAndFlush(c, trafficID, byteBuf);
    }

    @Override
    public void broadcastFile(String path, String file) throws IOException {
        this.getMessageExchanger(PassiveFileSenderExchanger.class).sendFile(this.c, path, file);
    }

    @Override
    public <T extends DTOSerializer> void broadcastDTO(String trafficID, T dto) {
        this.writeAndFlushDTO(this.c, trafficID, dto);
    }

    public Channel getChannel() {
        return c;
    }

}
