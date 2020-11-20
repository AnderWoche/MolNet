package de.moldiy.molnet.exchange.massageexchanger.file.passive;

public interface FileExchangerListener {

    void createNewFile(String path, long totalFileSize);

    void receiveFile(long currentFileSize, long totalFileSize);

    void fileSuccessfullyReceived(String path, long totalFileSize);
}
