package de.triology.universeadm.user.imports;

import com.google.common.collect.Lists;
import com.opencsv.bean.exceptionhandler.CsvExceptionHandler;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserSelfRemoveException;
import de.triology.universeadm.user.Users;
import org.junit.Test;
import org.mockito.Mock;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CSVParserTest {

    @Test
    public void testParse() {

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
    public void testKeepSpaces() {

        CSVUserDTO expUser = CSVUsers.createDent();
        expUser.setSurname("     " +expUser.getSurname());


        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("KeepSpaces.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals(expUser, userInputList.get(0));
    }

    @Test
    public void testDoubleQuotes() {
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
    public void testFieldLineBreaks() {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("LineBreaks.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertTrue(userInputList.get(0).getMail().contains("\n"));
    }

    @Test
    public void testDoubleQuotesInField() {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("DoubleQuotesField.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals("Arthur \"big\" Dent", userInputList.get(0).getDisplayname());
    }

    @Test
    public void testUmlauts() {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("Umlauts.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals("Arthür Dänß", userInputList.get(0).getDisplayname());
    }

    @Test
    public void testParseBoolean() {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(readTestFile("Boolean.csv"))
                .collect(Collectors.toList());

        assertEquals(5, userInputList.size());
    }

    @Test
    public void testReaderNull() {
        List<CSVUserDTO> userInputList = new CSVParser()
                .parse(null)
                .collect(Collectors.toList());

        assertTrue(userInputList.isEmpty());
    }

    @Test()
    public void testInvalidHeader() {
        ExecptionListener eh = mock(ExecptionListener.class);

        CSVParser parser = new CSVParser();
        parser.registerListener(eh);

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidHeader.csv"))
                .collect(Collectors.toList());

        assertEquals(0, userInputList.size());
        verify(eh, times(1)).notify(any());
    }

    @Test()
    public void testInvalidLineFieldLength() {
        ExecptionListener eh = mock(ExecptionListener.class);

        CSVParser parser = new CSVParser();
        parser.registerListener(eh);

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_NumberFields.csv"))
                .collect(Collectors.toList());

        assertEquals(3, userInputList.size());
        verify(eh, times(2)).notify(any());
    }

    @Test()
    public void testInvalidLineDelimiter() {
        ExecptionListener eh = mock(ExecptionListener.class);

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
