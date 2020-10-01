package de.moldiy.molnet;

import de.moldiy.molnet.client.Client;
import de.moldiy.molnet.exchange.MessageExchanger;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.server.Server;
import de.moldiy.molnet.server.authenticate.AuthenticateValidatorHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ServerClientTest {

    private int port;

    public static void main(String[] args) throws InterruptedException {
        int port = 9745;

        Server s = new Server(port, new AuthenticateValidatorHandler() {
            @Override
            public boolean authenticate(String[] args) {
                if (args.length == 2) {
                    if (args[0].equals("username")) {
                        return args[1].equals("pw");
                    }
                }
                return false;
            }

            public void loginSuccessful(ChannelHandlerContext ctx, String[] args) {
                System.out.println("login successful!");
            }

            @Override
            public void loginUnSuccessful(ChannelHandlerContext ctx) {
                System.out.println("login Unsuccessful -");
            }
        });

        s.loadMessageExchanger(new MessageExchanger<Server>() {
            @TrafficID(id = "cords")
            public void setCords(ChannelHandlerContext ctx, ByteBuf byteBuf) {
                System.out.println("jaas");
            }
        });

        s.bind();

        Client c = new Client("localhost", port);
        c.connectAndAuth("lol", "eins");
        c.authenticate("username", "pw");

        c.writeAndFlush("cords", c.getChannel().alloc().buffer().writeInt(100));

        c.authenticate("username", "pw");

        Thread.sleep(1000);

        c.getChannel().close();

//		new SpaceClient("localhost", port);

    }
}
