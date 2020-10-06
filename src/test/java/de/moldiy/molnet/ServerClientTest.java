package de.moldiy.molnet;

import de.moldiy.molnet.client.Client;
import de.moldiy.molnet.exchange.MessageHandler;
import de.moldiy.molnet.exchange.RightRestrictedMethodHandle;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.server.Server;
import de.moldiy.molnet.server.ServerChannelConnection;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ServerClientTest {

    private int port;

    public static void main(String[] args) {
        int port = 9745;


        class MessageReceiver {
        }
        Server s = new Server(port) {
            @Override
            protected void handleNoAccessRight(ChannelHandlerContext ctx, BitVector rightBits) {
            }
            @Override
            public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
                return null;
            }

            @Override
            protected void handleTrafficIDNotFound(String id) {
                System.out.println("trafficID not found!");
            }
        };
        s.loadMessageExchanger(new MessageReceiver() {
            //            @Rights(rights = {"admin", "normalo"})
            @TrafficID(id = "cords")
            public void setCords(ServerChannelConnection connection, ByteBuf byteBuf) {
                System.out.println("jaas");
            }
        });
        s.bind();

        MessageHandler clientMessageHandler = new MessageHandler() {
            @Override
            public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
                return null;
            }

            @Override
            protected void execMethod(RightRestrictedMethodHandle methodHandle, BitVector rightBits, ChannelHandlerContext ctx, ByteBuf byteBuf) {

            }

            @Override
            protected void handleTrafficIDNotFound(String id) {

            }
        };

        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                Client c = new Client("localhost", port) {

                    @Override
                    protected void handleNoAccessRight(ChannelHandlerContext ctx, BitVector rightBits) {
                    }
                    @Override
                    public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
                        return null;
                    }

                    @Override
                    protected void handleTrafficIDNotFound(String id) {

                    }
                };
                try {
                    c.connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                c.writeAndFlush("cords", c.getChannel().alloc().buffer().writeInt(100));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                c.getChannel().close();
            }).start();
        }
        System.gc();

//		new SpaceClient("localhost", port);

    }
}
