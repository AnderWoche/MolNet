package de.moldiy.molnet.blockingtest;

import de.moldiy.molnet.NetworkInterface;
import de.moldiy.molnet.exchange.TrafficID;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class BlockingMessageExchanger {

    @TrafficID(id = "block")
    public void net_blockingMessage(NetworkInterface networkInterface, ChannelHandlerContext ctx, ByteBuf byteBuf) {
        System.out.println("START");
//        pi();
        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        networkInterface.writeAndFlush(ctx.channel(), "response");
    }

    public static void pi() {
        double input = 10_000_000_000D;
        double sum = 0;
        for(double i = 0; i < input; i++) {
            if(i % 2 == 0) // if the remainder of `i/2` is 0
                sum += -1 / ( 2 * i - 1);
            else
                sum += 1 / (2 * i - 1);
        }
        System.out.println(sum);
    }
}
