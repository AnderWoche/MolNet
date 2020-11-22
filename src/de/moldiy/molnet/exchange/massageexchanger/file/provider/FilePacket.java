package de.moldiy.molnet.exchange.massageexchanger.file.provider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilePacket {

    private final List<File> files = new ArrayList<>();
    private final List<File> unmodifiableFiles = Collections.unmodifiableList(this.files);

    private final List<String> relativeFilePath = new ArrayList<>();
    private final List<String> relativeFilePathReadOnly = Collections.unmodifiableList(this.relativeFilePath);

    private long totalTransferSize = 0;

    public FilePacket(Path path) throws IOException {
        if(Files.isRegularFile(path)) {
            this.addFileWithRelativePath(path.toFile(), path.getFileName().toString());
        } else {
            Files.walk(path).filter(Files::isRegularFile).forEach(filePath -> {
                String pathString = path.toString();
                String eachPathString = filePath.toString();
                String relativePath = eachPathString.substring(pathString.length());

                this.addFileWithRelativePath(filePath.toFile(), relativePath);
            });
        }
    }

    private void addFileWithRelativePath(File file, String relativePath) {
        this.totalTransferSize += file.length();
        this.files.add(file);
        this.relativeFilePath.add(relativePath);
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
