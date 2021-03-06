package de.moldiy.molnet;

import de.moldiy.molnet.exchange.*;
import de.moldiy.molnet.exchange.massageexchanger.file.passive.FileExchangerListener;
import de.moldiy.molnet.exchange.massageexchanger.file.passive.PassiveFileReceiverExchanger;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerClientTest {

    private int port;

    private static long time;

    private static int createClients = 1;

    public static void main(String[] args) {
        int port = 6555;

        System.out.println("STRAT SERVER CLIENT TEST");


        class MessageReceiver {
        }
        Server s = new Server(port);
        s.loadMessageExchanger(new MessageReceiver() {
            final AtomicInteger i = new AtomicInteger();

            @Rights(rights = {"admin", "normalo"})
            @TrafficID(id = "cords")
            public void setCords(NetworkInterface writer, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                System.out.println("jaas");
//                if(i % 10 == 0) {
                System.out.println(i.incrementAndGet() + " TIME: " + ((System.currentTimeMillis() - time) / 1000F) + " sec.");
                System.out.println("[Server] Clients size: " + s.getAllClients().size());
//                }
//                if(i == createClients) {
//                    System.out.println(PooledByteBufAllocator.DEFAULT.dumpStats());
//                }
            }

            @Threaded
            @TrafficID(id = "getRights")
            public void setRights(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                BitVector bitVector = networkInterface.getRightIDFactory()
                        .addRightBits(new BitVector(), "admin", "normalo");
                networkInterface.getChannelIdentifierManager().setIdentifier(ctx.channel(), "rights", bitVector);
                System.out.println("rechte gegeben");
            }

            @RunOnChannelConnect
            private void sendFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                try {
                    networkInterface.writeFile(ctx.channel(), "C:\\Users\\david\\Desktop\\test1.mkv", "C:\\Users\\david\\Videos\\ping.mkv");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        s.bind().addListener((channelFuture) -> {
            if (channelFuture.isSuccess()) {
                System.out.println("[SEVRER] Sever ist Oben!");
            } else {
                System.out.println("Server cant start: " + channelFuture.cause());
            }
        });

        time = System.currentTimeMillis();
        for (int i = 0; i < createClients; i++) {
            new Thread(() -> {
                Client c = new Client("127.0.0.1", port, new EmptyMessageHandler());
                c.getMessageExchanger(PassiveFileReceiverExchanger.class).addListener(new FileExchangerListener() {
                    int i = 0;
                    @Override
                    public void createNewFile(String path, long totalFileSize) {

                    }

                    @Override
                    public void receiveFile(long currentFileSize, long totalFileSize) {
                        i++;
                        if(i == 2000) {
                            i = 0;
                            System.out.println(currentFileSize + "/" + totalFileSize);
                        }
                    }

                    @Override
                    public void fileSuccessfullyReceived(String path, long totalFileSize) {

                    }
                });
                connectToServer(c);
            }).start();
        }
    }

    public static void connectToServer(Client c) {
        ChannelFuture channelFuture = c.connect();
        channelFuture.addListener((channelF) -> {
            if (channelF.isSuccess()) {
                c.writeAndFlush(c.getChannel(), "cords", PooledByteBufAllocator.DEFAULT.buffer().writeInt(100));
                c.writeAndFlush(c.getChannel(), "getRights", PooledByteBufAllocator.DEFAULT.buffer().writeInt(100));
                c.writeAndFlush(c.getChannel(), "cords", PooledByteBufAllocator.DEFAULT.buffer().writeInt(100));
//                c.getChannel().close();
            } else {
//                System.out.println("[Client] cant connect to server! cause: " + channelF.cause());
                Thread.sleep(1000);
                connectToServer(c);
            }
        });
    }
}
