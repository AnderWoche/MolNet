package de.moldiy.molnet.exchange.massageexchanger.file.provider;

public class FileDownloadFuture {

    private final boolean success;
    private final Throwable cause;

    public FileDownloadFuture(boolean success, Throwable cause) {
        this.success = success;
        this.cause = cause;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public boolean isFailure() {
        return !this.success;
    }

    public Throwable cause() {
        return this.cause;
    }

}
