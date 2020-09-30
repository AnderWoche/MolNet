package de.moldiy.molnet.client;

import de.moldiy.molnet.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author David Humann (Moldiy)
 */
public abstract class Client extends AuthenticateHandler {

	private final Client that = this;

	private final String host;
	private final int port;

	private final Bootstrap bootstrap;

	private Channel c;

	private final MessageExchangerManager exchangerManager = new MessageExchangerManager();

	public Client(String host, int port) {
		this.host = host;
		this.port = port;

		this.bootstrap = new Bootstrap();
		this.bootstrap.group(new NioEventLoopGroup());
		this.bootstrap.channel(NioSocketChannel.class);
		this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			protected void initChannel(SocketChannel ch) {
				ch.pipeline().addFirst("decoder", new MassageReader());
				ch.pipeline().addFirst("encoder", new MassageWriter());

				ch.pipeline().addLast("auth", that);

				ch.pipeline().addLast("handler", new MessageHandler(exchangerManager));
			};
		});
	}

	public void connect() throws InterruptedException {
		this.c = this.bootstrap.connect(this.host, this.port).sync().channel();
	}

	public void connectAndAuth(String... args) throws InterruptedException {
		this.connect();
		super.authenticate(args);
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

	public void loadMessageExchanger(ClientMassageExchanger clientMassageExchanger) {
		this.exchangerManager.loadMassageExchanger(clientMassageExchanger);
	}

	@Override
	public void loginSuccessful(ChannelHandlerContext ctx) {

	}

	@Override
	public void loginUnSuccessful(ChannelHandlerContext ctx) {

	}

	public Channel getChannel() {
		return c;
	}
}
