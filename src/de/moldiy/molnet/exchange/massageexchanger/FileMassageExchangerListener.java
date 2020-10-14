package de.moldiy.molnet.exchange.massageexchanger;

public interface FileMassageExchangerListener {

    void createNewFile(String path, long totalFileSize);

    void receiveFile(long currentFileSize, long totalFileSize);
}
