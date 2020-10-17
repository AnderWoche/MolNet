package de.moldiy.molnet;

import de.moldiy.molnet.exchange.RunOnChannelConnect;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

public class ServerTest {

    public static void main(String[] args) {
        class MessageReceiver {
        }
        Server s = new Server(6555, new ServerMessageHandler());
        s.loadMessageExchanger(new MessageReceiver() {
            @RunOnChannelConnect
            public void sendFile(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
                String path = "C:\\Users\\david\\Desktop\\test1.mvk";
                String fileRead = "C:\\Users\\david\\Desktop\\Video.mkv";
                try {
                    networkInterface.writeFile(ctx.channel(), path, fileRead);
//                    networkInterface.writeFile(ctx.channel(), path, fileRead);
//                    networkInterface.writeFile(ctx.channel(), path, fileRead);
//                    networkInterface.writeFile(ctx.channel(), path, fileRead);
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

//        new Thread(new Runnable() {
//            private final Runtime runtime = Runtime.getRuntime();
//            private final NumberFormat format = NumberFormat.getInstance();
//            @Override
//            public void run() {
//                while (true) {
//                    System.out.println("memoryusage: " + format.format(runtime.totalMemory() / 1024) + " / " + format.format(runtime.maxMemory() / 1024));
//                    try {
//                        Thread.sleep(10_000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
    }
}
