package de.moldiy.molnet.server;


import de.moldiy.molnet.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public abstract class Server extends ServerMessageHandler {

    private final Server that = this;

    private final ServerBootstrap serverBootstrap;

    private final int port;

    private final ChannelGroup allClients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public Server(int port) {
        this.port = port;
        super.setServer(this);
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

                ch.pipeline().addLast("handler", that);

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

    public void flush(ChannelHandlerContext ctx) {
        ctx.channel().flush();
    }

    public ChannelGroup getAllClients() {
        return this.allClients;
    }

}
