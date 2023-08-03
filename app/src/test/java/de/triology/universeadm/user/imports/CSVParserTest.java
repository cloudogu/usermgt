package de.triology.universeadm.user.imports;

import com.google.common.collect.Lists;
import com.opencsv.exceptions.CsvException;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CSVParserTest {

    @Test
    public void testParse() throws BadArgumentException {

        List<CSVUserDTO> expUsers = Lists.newArrayList(
                CSVUsers.createDent(),
                CSVUsers.createTrillian()
        );

        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("ImportUsers.csv"))
                .collect(Collectors.toList());

        assertEquals(expUsers.size(), userInputList.size());

        for (int i = 0; i < expUsers.size(); i++) {
            assertEquals(expUsers.get(i), userInputList.get(i));
        }

    }

    @Test
    public void testKeepSpaces() throws BadArgumentException {

        CSVUserDTO expUser = CSVUsers.createDent();
        expUser.setSurname("     " +expUser.getSurname());


        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("KeepSpaces.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals(expUser, userInputList.get(0));
    }

    @Test
    public void testDoubleQuotes() throws BadArgumentException {
        List<CSVUserDTO> expUsers = Lists.newArrayList(
                CSVUsers.createDent(),
                CSVUsers.createTrillian()
        );

        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("DoubleQuotes.csv"))
                .collect(Collectors.toList());

        assertEquals(expUsers.size(), userInputList.size());

        for (int i = 0; i < expUsers.size(); i++) {
            assertEquals(expUsers.get(i), userInputList.get(i));
        }
    }

    @Test
    public void testFieldLineBreaks() throws BadArgumentException {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("LineBreaks.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertTrue(userInputList.get(0).getMail().contains("\n"));
    }

    @Test
    public void testDoubleQuotesInField() throws BadArgumentException {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("DoubleQuotesField.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals("Arthur \"big\" Dent", userInputList.get(0).getDisplayname());
    }

    @Test
    public void testUmlauts() throws BadArgumentException {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("Umlauts.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals("Arthür Dänß", userInputList.get(0).getDisplayname());
    }

    @Test
    public void testEmptyGivenName() throws BadArgumentException {
        CSVUserDTO expUser = CSVUsers.createDent();
        expUser.setGivenname("");

        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("EmptyGivenName.csv"))
                .collect(Collectors.toList());

        assertEquals(expUser, userInputList.get(0));
    }

    @Test
    public void testParseBoolean() throws BadArgumentException {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("Boolean.csv"))
                .collect(Collectors.toList());

        assertEquals(5, userInputList.size());
    }

    @Test(expected=BadArgumentException.class)
    public void testReaderNull() throws BadArgumentException {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(null)
                .collect(Collectors.toList());

        assertTrue(userInputList.isEmpty());
    }

    @Test(expected=BadArgumentException.class)
    public void testInvalidHeader() throws BadArgumentException {
        CSVParser parser = new CSVParser();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidHeader.csv"))
                .collect(Collectors.toList());

        assertTrue(userInputList.isEmpty());
    }

    @Test()
    public void testInvalidLineFieldLength() throws BadArgumentException {
        ExceptionListener<CsvException> eh = mock(ExceptionListener.class);

        CSVParser parser = new CSVParser();
        parser.registerListener(eh);

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_NumberFields.csv"))
                .collect(Collectors.toList());

        assertEquals(3, userInputList.size());
        verify(eh, times(2)).notify(any());
    }

    @Test()
    public void testMissingRequiredField() throws BadArgumentException {
        ExceptionListener<CsvException> eh = mock(ExceptionListener.class);

        CSVParser parser = new CSVParser();
        parser.registerListener(eh);

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_MissingRequiredField.csv"))
                .collect(Collectors.toList());

        assertEquals(1, userInputList.size());
        verify(eh, times(1)).notify(any());
    }

    @Test()
    public void testInvalidLineDelimiter() throws BadArgumentException {
        ExceptionListener<CsvException> eh = mock(ExceptionListener.class);

        CSVParser parser = new CSVParser();
        parser.registerListener(eh);

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_Delimiter.csv"))
                .collect(Collectors.toList());

        assertEquals(2, userInputList.size());
        verify(eh, times(1)).notify(any());
    }


    private InputStreamReader readTestFile(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();

        File file = Optional
                .ofNullable(classLoader.getResource("csvimports/" + filename))
                .map(URL::getFile)
                .map(File::new)
                .orElse(null);

        assertNotNull(file);

        try {
            InputStream inputStream = Files.newInputStream(file.toPath());
            return new InputStreamReader(inputStream);
        } catch (IOException e) {
            fail("Unable to read test file: " + e.getMessage());

            return null;
        }
    }

}
