package de.moldiy.molnet;

import de.moldiy.molnet.exchange.massageexchanger.file.provider.FileDownloadFuture;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.FileDownloadListener;
import de.moldiy.molnet.exchange.massageexchanger.file.provider.FileDownloadProcessor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ServerClientProviderTest {

    public static void main(String[] args) {
        new ServerClientProviderTest().test_provider_test();
    }

    public void test_provider_test() {

        Server server = new Server(3458);

        try {
            server.provideFile("fileName", Paths.get("C:\\Users\\david\\Videos\\Ein Video.mkv")); // C:\Users\david\Videos\Ein Video.mkv
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.bind();

        Client client = new Client("localhost", 3458);

        client.connect().addListener((channelListener) -> {
            if(channelListener.isSuccess()) {
                System.out.println("[CLIENT] connected");
            }
        });

        new Thread(() -> {
            System.out.println("send");
            FileDownloadProcessor fileDownloadProcessor = client.requestFile(client.getChannel(), "fileName", Paths.get("C:\\Users\\david\\Desktop"));
            fileDownloadProcessor.addListener(new FileDownloadListener() {
                @Override
                public void newFile(File file) {
                    System.out.println("new File: " + file);
                }
                int i = 0;
                @Override
                public void bytesReceived(long currentSize, long totalSize) {
                    i++;
                    if(i == 500) {
                        System.out.println("Downloading... " + currentSize + " / " + totalSize);
                        i = 0;
                    }
                }

                @Override
                public void done(FileDownloadFuture fileDownloadFuture) {
                    if(fileDownloadFuture.isSuccess()) {
                        System.out.println("Files successful downloaded");
                    } else {
                        fileDownloadFuture.cause().printStackTrace();
                    }
                }
            });

            fileDownloadProcessor.sync();

            System.out.println("TEST!");
        }).start();

    }
}
