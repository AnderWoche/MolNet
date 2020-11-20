package de.moldiy.molnet.exchange.massageexchanger.file.active;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FilePacketReader {

    private final List<File> files;

    private File currentFile;
    private FileInputStream fileInputStream;

    private int currentIndex = 0;

    public FilePacketReader(FilePacket filePacked) {
        this.files = filePacked.getFiles();
    }

    public boolean hasNextFile() {
        return currentIndex < this.files.size();
    }

    public File nextFile() throws IOException {
        if(this.fileInputStream != null) {
            this.fileInputStream.close();
        }
        this.currentFile = this.files.get(this.currentIndex++);
        this.fileInputStream = new FileInputStream(this.currentFile);

        return this.currentFile;
    }

    public int read(byte[] b) throws IOException {
        return this.fileInputStream.read(b);
    }

    public void close() throws IOException {
        this.fileInputStream.close();
    }

    public boolean hasCurrentFile() {
        return this.currentFile != null;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
