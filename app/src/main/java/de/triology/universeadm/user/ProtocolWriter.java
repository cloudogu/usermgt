package de.triology.universeadm.user;

import org.joda.time.LocalDateTime;

import java.io.FileWriter;
import java.io.IOException;

public class ProtocolWriter {
    private final String fileName;
    private final FileWriterBuilder writerBuilder;

    public static class FileWriterBuilder {
        public FileWriter build(final String fileName) throws IOException {
            return new FileWriter(fileName);
        }
    }

    public ProtocolWriter() {
        this("/var/lib/usermgt/protocol/user-import-protocol", new FileWriterBuilder());
    }

    public ProtocolWriter(String fileName) {
        this(fileName, new FileWriterBuilder());
    }

    public ProtocolWriter(String fileName, FileWriterBuilder writerBuilder) {
        this.fileName = fileName;
        this.writerBuilder = writerBuilder;
    }

    public void writeLine(String line) throws IOException {
        try (FileWriter writer = this.writerBuilder.build(this.fileName)) {
            writer.append(LocalDateTime.now() + ": ");
            writer.append(line);
            writer.append("\n");
        }
    }

}
