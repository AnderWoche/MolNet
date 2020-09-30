package de.moldiy.spaceexplorer.server;


import io.netty.bootstrap.ServerBootstrap;
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

public abstract class Server extends AuthenticateValidatorHandler {

    private final Server that = this;

    private ServerBootstrap serverBootstrap;

    private int port;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private final ChannelGroup allClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final MessageExchangerManager exchangerManager = new MessageExchangerManager();

    private final ConnectionIdentifierManager identifierManager = new ConnectionIdentifierManager();

    public Server(int port) {
        this.port = port;
        this.serverBootstrap = new ServerBootstrap();
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
