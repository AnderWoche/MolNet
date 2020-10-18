package de.moldiy.molnet;

import de.moldiy.molnet.exchange.Rights;
import de.moldiy.molnet.exchange.RunOnChannelConnect;
import de.moldiy.molnet.exchange.TrafficID;
import de.moldiy.molnet.utils.BitVector;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.FileNotFoundException;

public class ServerClientTest2 {

    public static void main(String[] args) {

        Server server = new Server(6334, new MessageHandler() {
            @Override
            public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
                return super.getNetworkInterface().getChannelIdentifierManager().getIdentifier("rights", ctx.channel());
            }

            @Override
            protected void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id) {

            }

            @Override
            protected void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx) {
                System.out.println("KEINE RECHTE! ZU DER TRAFFICID: " + trafficID);
            }
        });

        server.bind();

        class LOL {
        }
        server.loadMessageExchanger(new LOL() {
            @Rights(rights = {"admin", "normlao"})
            @TrafficID(id = "cords")
            public void getCords(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                System.out.println("x:" + byteBuf.readInt());
                System.out.println("y:" + byteBuf.readInt());

            }

            @TrafficID(id = "login")
            public void getRights(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                BitVector bitVector = networkInterface.getRightIDFactory().addRightBits(new BitVector(), "admin", "normlao");

                networkInterface.getChannelIdentifierManager().setIdentifier(ctx.channel(), "rights", bitVector);
            }
            @RunOnChannelConnect
            public void sendFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                try {
                    networkInterface.writeFile(ctx.channel(), "", "");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        });

        Client client = new Client("localhost", 6334, new MessageHandler() {
            @Override
            public BitVector getRightBitsFromChannel(ChannelHandlerContext ctx) {
                return null;
            }

            @Override
            protected void handleTrafficIDNotFound(ChannelHandlerContext ctx, String id) {

            }

            @Override
            protected void handleNoAccessRight(String trafficID, ChannelHandlerContext ctx) {

            }
        });

        client.connect().addListener((channelFuture) -> {
            if(channelFuture.isSuccess()) {

                client.writeAndFlush(client.getChannel(), "login");

                ByteBuf byteBuf = client.getChannel().alloc().buffer();

                byteBuf.writeInt(500);
                byteBuf.writeInt(1001);

                client.writeAndFlush(client.getChannel(), "cords", byteBuf);
            }
        });
    }

}
