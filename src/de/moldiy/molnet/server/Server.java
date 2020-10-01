package de.moldiy.molnet.server;


import de.moldiy.molnet.*;
import de.moldiy.molnet.exchange.MessageHandler;
import de.moldiy.molnet.exchange.NetworkExchanger;
import de.moldiy.molnet.server.authenticate.AuthenticateValidatorHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server extends NetworkExchanger<Server> implements ChannelHandler {

    private final Server that = this;

    private final ServerBootstrap serverBootstrap;

    private final int port;

    private final ChannelGroup allClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final ConnectionIdentifierManager identifierManager = new ConnectionIdentifierManager();

    public Server(int port, AuthenticateValidatorHandler authenticateValidatorHandler) {
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

                ch.pipeline().addLast("channelTracker", that);

                ch.pipeline().addLast("authenticate", authenticateValidatorHandler);

                ch.pipeline().addLast("handler", new MessageHandler(exchangerManager));

                allClients.add(ch);
            }
        });
        this.serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        this.serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);

        authenticateValidatorHandler.getUserSuccessAuthenticatedEvent().addListener(event ->
                this.identifierManager.addConnection(event.ctx, event.args));
    }

    public Server(int port) {
        this(port, new AuthenticateValidatorHandler() {
            @Override
            public boolean authenticate(String[] args) {
                return true;
            }
        });
    }

    public void bind() {
        try {
            this.serverBootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    protected NetworkExchanger<Server> getNetworkExchanger() {
        return this;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.identifierManager.removeConnection(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.fireExceptionCaught(cause);
    }

    public ChannelGroup getAllClients() {
        return this.allClients;
    }
}
