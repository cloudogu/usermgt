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

    public static final String COULD_NOT_CLOSE_PROTOCOL = "Could not close Protocol";

    public static final String COULD_NOT_OPEN_PROTOCOL = "Could not open Protocol";

    private static final Logger logger = LoggerFactory.getLogger(ProtocolWriter.class);
    private FileWriter writer;

    public static class FileWriterBuilder {
        public FileWriter build(final String fileName) throws IOException {
            return new FileWriter(fileName, true);
        }
    }

    public static class ProtocolWriterBuilder {
        public ProtocolWriter build(String fileName) {
            return new ProtocolWriter(fileName, new FileWriterBuilder());
        }
    }

    public ProtocolWriter() {
        this(USER_IMPORT_FILE_NAME, new FileWriterBuilder());
    }

    public ProtocolWriter(String fileName, FileWriterBuilder writerBuilder) {
        try {
            writer = writerBuilder.build(fileName);
        } catch (IOException e) {
            logger.error(COULD_NOT_OPEN_PROTOCOL, e);
        }
    }

    public void writeLine(String content) {
        if (writer != null) {
            try {
                writer.append(String.format("%s: ", LocalDateTime.now()));
                writer.append(content);
                writer.append(CSQ);
            } catch (IOException e) {
                logger.error(COULD_NOT_WRITE_PROTOCOL_ENTRY, e);
            }
        }
    }

    public void close() {
        if (writer != null) {
            try {
                writer.close();
                writer = null;
            } catch (IOException e) {
                logger.error(COULD_NOT_CLOSE_PROTOCOL, e);
            }
        }
    }

}
