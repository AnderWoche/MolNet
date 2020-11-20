package de.moldiy.molnet;

import java.io.IOException;
import java.nio.file.Paths;

public class ServerClientProviderTest {

    public static void main(String[] args) {
        new ServerClientProviderTest().test_provider_test();
    }

    public void test_provider_test() {

        Server server = new Server(3458);

        try {
            server.provideFile("fileName", Paths.get("C:\\Users\\david\\Videos\\Ein Video.mkv"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.bind();

        Client client = new Client("localhost", 3458);

        client.requestFile(client.getChannel(), "filename", Paths.get("C:\\Users\\david\\Desktop"));
        client.connect();
    }
}
