package de.moldiy.molnet;

import de.moldiy.molnet.exchange.DTOSerializer;
import de.moldiy.molnet.exchange.TrafficID;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

public class DTOTest {

    public static class TestDTO implements DTOSerializer {

        private int id;

        @Override
        public ByteBuf serialize(ByteBuf byteBuf) {
            return byteBuf.writeInt(id);
        }

        @Override
        public void deserialize(ByteBuf byteBuf) {
            this.id = byteBuf.readInt();
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public class Exchanger {
        @TrafficID(id = "id")
        public void net(NetworkInterface networkInterface, ChannelHandlerContext ctx, TestDTO testDTO) {
            System.out.println("get ID: " + testDTO.getId());
        }
    }

    public static void main(String[] args) {
        new DTOTest().dtoTest();
    }

    @Test
    public void dtoTest() {
        Server server = new Server(3458);
        server.loadMessageExchanger(new Exchanger());
        server.bind();

        Client client = new Client("localhost", 3458);

        client.connect().addListener((channelListener) -> {
            if(channelListener.isSuccess()) {
                System.out.println("[CLIENT] connected");
                TestDTO dto = client.obtainDTO(TestDTO.class);
                dto.setId(100100);
                client.writeAndFlush(client.getChannel(), "id", dto);
            }
        });



    }

}
