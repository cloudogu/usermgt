package de.triology.universeadm.user;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.io.FileWriter;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class ProtcolWriterTest {
    private ProtocolWriter.FileWriterBuilder mockWriterBuilder;
    private ProtocolWriter protocolWriter;
    private FileWriter fileWriter;

    @Before
    public void createBuilder() throws IOException {
        this.mockWriterBuilder = mock(ProtocolWriter.FileWriterBuilder.class);
        this.protocolWriter = new ProtocolWriter("myfilename.protocol", this.mockWriterBuilder);
        this.fileWriter = mock(FileWriter.class);
        when(this.mockWriterBuilder.build("myfilename.protocol")).thenReturn(fileWriter);
    }

    @Test
    public void writesLineSuccessfully() throws IOException {
        this.protocolWriter.writeLine("test line");
        verify(this.mockWriterBuilder).build("myfilename.protocol");
        InOrder inOrder = inOrder(this.fileWriter);
        inOrder.verify(this.fileWriter, times(1)).append(matches("([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}: )"));
        inOrder.verify(this.fileWriter).append("test line");
        inOrder.verify(this.fileWriter).append("\n");
    }

    @Test(expected = IOException.class)
    public void failOnOpen() throws IOException {
        this.mockWriterBuilder = mock(ProtocolWriter.FileWriterBuilder.class);
        this.protocolWriter = new ProtocolWriter("myfilename.protocol", this.mockWriterBuilder);
        when(this.mockWriterBuilder.build("myfilename.protocol")).thenThrow(new IOException());
        this.protocolWriter.writeLine("test line");
    }

}
