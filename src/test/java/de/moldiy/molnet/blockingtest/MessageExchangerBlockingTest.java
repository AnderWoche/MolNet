package de.moldiy.molnet.blockingtest;

import de.moldiy.molnet.Client;
import de.moldiy.molnet.Server;
import org.junit.Test;

public class MessageExchangerBlockingTest {

    public static void main(String[] args) {
        new MessageExchangerBlockingTest().messageExchangerBlockingTest();
    }

    @Test
    public void messageExchangerBlockingTest() {

        Server server = new Server(7856);

        server.loadMessageExchanger(new BlockingMessageExchanger());

        server.bind().addListener((channelFuture) -> {
            if(channelFuture.isSuccess()) {
                System.out.println("server OBEN!");
            }
        });


        new Thread(() -> {
            for(int i = 0; i < 50; i++) {
                Client c = new Client("localhost", 7856);
                c.loadMessageExchanger(new ClientMessageExchanger());
                c.connect().addListener((channelFuture) -> {
                    if(channelFuture.isSuccess()) {
                        System.out.println("send Request");
                        c.writeAndFlush(c.getChannel(), "block");
                    } else {
                        System.out.println("client cant connect");
                    }
                });
            }
        }).start();

    }
}
