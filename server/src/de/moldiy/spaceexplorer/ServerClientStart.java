package de.moldiy.spaceexplorer;

import de.moldiy.spaceexplorer.client.Client;
import de.moldiy.spaceexplorer.client.ClientMassageExchanger;
import de.moldiy.spaceexplorer.server.Server;
import de.moldiy.spaceexplorer.server.ServerMessageExchanger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ServerClientStart {

    private int port;


    public static void main(String[] args) throws InterruptedException {
        int port = 9745;

        Server s = new Server(port) {
            @Override
            public boolean authenticate(String[] args) {
                if (args.length == 2) {
                    if (args[0].equals("username")) {
                        return args[1].equals("pw");
                    }
                }
                return false;
            }

            @Override
            public void loginSuccessful(ChannelHandlerContext ctx, String[] args) {
                System.out.println("login successful!");
            }

            @Override
            public void loginUnSuccessful(ChannelHandlerContext ctx) {
                System.out.println("login Unsuccessful -");
            }
        };

        s.loadMessageExchanger(new ServerMessageExchanger() {
            @TrafficID(id = "cords")
            public void setCords(ChannelHandlerContext ctx, ByteBuf byteBuf) {
                System.out.println("jaaa");
            }
        });

        s.bind();

        Client c = new Client("localhost", port){};
        c.connectAndAuth("lol", "eins");
        c.authenticate("username", "pw");

        c.writeAndFlush("cords", c.getChannel().alloc().buffer().writeInt(100));

//		new SpaceClient("localhost", port);

    }
}
