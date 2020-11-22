package de.moldiy.molnet.exchange.massageexchanger.file.provider;

import java.io.File;

public interface FileDownloadListener {

    default void newFile(File file) {

    }

    default void bytesReceived(long currentSize, long totalSize) {

    }

    default void done(FileDownloadFuture fileDownloadFuture) {

    }
}
