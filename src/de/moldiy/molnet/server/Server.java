package de.moldiy.molnet.server;


import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.moldiy.molnet.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server extends AuthenticateValidatorHandler {

    private final Server that = this;

    private final ServerBootstrap serverBootstrap;

    private final int port;

    private final ChannelGroup allClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final MessageExchangerManager exchangerManager = new MessageExchangerManager();

    private final ConnectionIdentifierManager identifierManager = new ConnectionIdentifierManager();

    public Server(int port) {
        this.port = port;
        this.serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        this.serverBootstrap.group(bossGroup, workerGroup);
        this.serverBootstrap.channel(NioServerSocketChannel.class);
        this.serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) {
                ch.pipeline().addFirst("decoder", new MassageReader());
                ch.pipeline().addFirst("encoder", new MassageWriter());

                ch.pipeline().addLast("authenticate", that);

                ch.pipeline().addLast("handler", new MessageHandler(exchangerManager));

                allClients.add(ch);
            }
        });
        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
    }

    @Subscribe
    public void loginin(ChannelHandlerContext ctx, String[] args) {

    }

    public void bind() {
        try {
            this.serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void loadMessageExchanger(ServerMessageExchanger serverMessageExchanger) {
        this.exchangerManager.loadMassageExchanger(serverMessageExchanger);
    }

    public void broadCastMassage(String trafficID, ByteBuf byteBuf) {
        this.getAllClients().writeAndFlush(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public void write(ChannelHandlerContext ctx, String trafficID, ByteBuf byteBuf) {
        ctx.channel().write(NettyByteBufUtil.addStringBeforeMassage(trafficID, byteBuf));
    }

    public void writeAndFlush(ChannelHandlerContext ctx, String trafficID, ByteBuf byteBuf) {
        this.write(ctx, trafficID, byteBuf);
        ctx.flush();
    }

    @Override
    public void loginSuccessful(ChannelHandlerContext ctx, String[] args) {
        this.identifierManager.addConnection(ctx, args);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        this.identifierManager.removeConnection(ctx);
        super.channelUnregistered(ctx);
    }

    @Override
    public void loginUnSuccessful(ChannelHandlerContext ctx) {

    }

    @Override
    public boolean authenticate(String[] args) {
        return true;
    }

    public ChannelGroup getAllClients() {
        return this.allClients;
    }

    // verbinden
    // pw und unsename schicken
    // eine Abstracte Methode die den username und pw ueberprueft
    // wenn richtig wird ein accound zurück gegeben.

    // Account
    // hat ein right Also rights ein String
    // es hat ein Name
    // es hat eine String UUID

    //wenn null zurück gegeben wird wird die Verbindung Unterbrochen.

}
