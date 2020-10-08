package de.moldiy.molnet;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server extends NetworkInterface {

    private final ServerBootstrap serverBootstrap;

    private final int port;

    private final ChannelGroup allClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final MessageHandler messageHandler;

    public Server(int port, MessageHandler messageHandler) {
        this.port = port;
        this.messageHandler = messageHandler;

        messageHandler.setNetworkInterface(this);

        this.serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.serverBootstrap.group(bossGroup, workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        this.serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                ch.pipeline().addFirst("decoder", new MessageDecoder());
                ch.pipeline().addFirst("encoder", new MessageEncoder());

                ch.pipeline().addLast("handler", messageHandler);

                allClients.add(ch);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                allClients.remove(ctx.channel());
                super.channelInactive(ctx);
            }
        });
        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

    }

    public ChannelFuture bind() {
        return this.serverBootstrap.bind(port);
    }

    @Override
    public void broadCastMassage(String trafficID, ByteBuf byteBuf) {
        this.getAllClients().writeAndFlush(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public ChannelGroup getAllClients() {
        return this.allClients;
    }

    public void loadMessageExchanger(Object object) {
        this.messageHandler.loadMessageExchanger(object);
    }

}
