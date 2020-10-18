package de.moldiy.molnet.exchange.massageexchanger;

public interface FileMessageExchangerListener {

    void createNewFile(String path, long totalFileSize);

    void receiveFile(long currentFileSize, long totalFileSize);

    void fileSuccessfullyReceived(String path, long totalFileSize);
}
