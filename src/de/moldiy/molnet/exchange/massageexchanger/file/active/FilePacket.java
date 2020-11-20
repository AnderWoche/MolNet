package de.moldiy.molnet.exchange.massageexchanger.file.active;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePacket {

    private final List<File> files = new ArrayList<>();
    private final List<String> relativeFilePath = new ArrayList<>();

    private final List<File> unmodifiableFiles = Collections.unmodifiableList(this.files);
    private final List<String> relativeFilePathReadOnly = Collections.unmodifiableList(this.relativeFilePath);

    private long totalTransferSize = 0;

    public FilePacket(Path path) throws IOException {
        if(Files.isRegularFile(path)) {
            File file = path.toFile();
            this.totalTransferSize += file.length();
            this.files.add(path.toFile());
        } else {
            Files.walk(path).filter(Files::isRegularFile).forEach(filePath -> {
                String pathString = path.toString();
                String eachPathString = filePath.toString();
                this.relativeFilePath.add(eachPathString.substring(pathString.length()));

                File file = filePath.toFile();
                this.totalTransferSize += file.length();
                this.files.add(file);
            });
        }
    }

    public List<File> getFiles() {
        return this.unmodifiableFiles;
    }

    public List<String> getRelativeFilePath() {
        return this.relativeFilePathReadOnly;
    }

    public long getTotalTransferSize() {
        return totalTransferSize;
    }
}
