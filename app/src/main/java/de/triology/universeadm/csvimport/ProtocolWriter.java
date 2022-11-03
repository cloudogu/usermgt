package de.triology.universeadm.csvimport;

import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;



public class ProtocolWriter {
    private static final String USER_IMPORT_FILE_NAME = "/var/lib/usermgt/protocol/user-import-protocol";
    public static final String CSQ = "\n";
    public static final String COULD_NOT_WRITE_PROTOCOL_ENTRY = "Could not write Protocol Entry";

    private static final Logger logger = LoggerFactory.getLogger(ProtocolWriter.class);
    private final String fileName;
    private final FileWriterBuilder writerBuilder;

    public static class FileWriterBuilder {
        public FileWriter build(final String fileName) throws IOException {
            return new FileWriter(fileName, true);
        }
    }

    public ProtocolWriter(String fileName, FileWriterBuilder writerBuilder) {
        this.fileName = fileName;
        this.writerBuilder = writerBuilder;
    }

    public void writeLine(String line) {
        try (FileWriter writer = this.writerBuilder.build(this.fileName)) {
            writer.append(String.format("%s: ",LocalDateTime.now()));
            writer.append(line);
            writer.append(CSQ);
        } catch (IOException e) {
            logger.error(COULD_NOT_WRITE_PROTOCOL_ENTRY, e);
        }
    }
}
