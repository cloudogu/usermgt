package de.triology.universeadm.csvimport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.Mockito.*;

public class ProtcolWriterTest {
    private static final String CSQ = "\n";
    private static final String TESTFILE = "myfilename.protocol";
    private static final String TESTLINE = "test line";
    private static final String DATE_REGEX = "([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}: )";
    private ProtocolWriter.FileWriterBuilder mockWriterBuilder;
    private ProtocolWriter protocolWriter;
    private FileWriter fileWriter;

    @Before
    public void createBuilder() throws IOException {
        this.mockWriterBuilder = mock(ProtocolWriter.FileWriterBuilder.class);
        this.fileWriter = mock(FileWriter.class);
        when(this.mockWriterBuilder.build(TESTFILE)).thenReturn(fileWriter);
        this.protocolWriter = new ProtocolWriter(TESTFILE, this.mockWriterBuilder);
    }

    @Test
    public void writesLineSuccessfully() throws IOException {
        this.protocolWriter.writeLine(TESTLINE);
        verify(this.mockWriterBuilder).build(TESTFILE);
        InOrder inOrder = inOrder(this.fileWriter);
        inOrder.verify(this.fileWriter, times(1)).append(matches(DATE_REGEX));
        inOrder.verify(this.fileWriter).append(TESTLINE);
        inOrder.verify(this.fileWriter).append(CSQ);

    }

    @Test
    public void failOnOpen() throws Exception {
        final Field field = ProtocolWriter.class.getDeclaredField("logger");
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        final Logger loggerMock = mock(Logger.class);
        field.set(null, loggerMock);
        when(this.mockWriterBuilder.build(TESTFILE)).thenThrow(new IOException());
        new ProtocolWriter(TESTFILE, mockWriterBuilder);
        final String COULD_NOT_WRITE_PROTOCOL_ENTRY = "Could not open Protocol";

        verify(loggerMock).error(eq(COULD_NOT_WRITE_PROTOCOL_ENTRY), Matchers.<IOException>any());
    }


}
