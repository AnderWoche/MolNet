package de.moldiy.molnet;

import de.moldiy.molnet.exchange.massageexchanger.FileMessageReceiverExchanger;
import de.moldiy.molnet.exchange.massageexchanger.FileMessageExchangerListener;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;

public class ClientTest {

    public static void main(String[] args) {
        Client c = new Client("127.0.0.1", 6555, new EmptyMessageHandler());
        c.getMessageExchanger(FileMessageReceiverExchanger.class).addListener(new FileMessageExchangerListener() {
            int files = 0;

            int i = 0;
            @Override
            public void createNewFile(String path, long totalFileSize) {
                System.out.println("create New File: " + files);
                files++;
            }

            @Override
            public void receiveFile(long currentFileSize, long totalFileSize) {
                if ((totalFileSize - currentFileSize) == 0) {
                    System.out.println(currentFileSize + "/" + totalFileSize);
                    System.out.println("FERTIG");
                }
                i++;
                if(i == 3000) {
                    i = 0;
                    System.out.println(currentFileSize + "/" + totalFileSize);
                }
            }

            @Override
            public void fileSuccessfullyReceived(String path, long totalFileSize) {

            }
        });
        connectToServer(c);

//        new Thread(new Runnable() {
//            private final Runtime runtime = Runtime.getRuntime();
//            private final NumberFormat format = NumberFormat.getInstance();
//
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
