package de.triology.universeadm.user.imports;

import com.google.common.collect.Lists;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.Test;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class CSVParserTest {

    @Test
    public void testParse() throws CsvRequiredFieldEmptyException {

        List<CSVUserDTO> expUsers = Lists.newArrayList(
                CSVUsers.createDent(),
                CSVUsers.createTrillian()
        );

        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("ImportUsers.csv"))
                .collect(Collectors.toList());

        assertEquals(expUsers.size(), userInputList.size());

        for (int i = 0; i < expUsers.size(); i++) {
            assertEquals(expUsers.get(i), userInputList.get(i));
        }

    }

    @Test
    public void testKeepSpaces() throws CsvRequiredFieldEmptyException {

        CSVUserDTO expUser = CSVUsers.createDent();
        expUser.setSurname("     " +expUser.getSurname());


        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("KeepSpaces.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals(expUser, userInputList.get(0));
    }

    @Test
    public void testDoubleQuotes() throws CsvRequiredFieldEmptyException {
        List<CSVUserDTO> expUsers = Lists.newArrayList(
                CSVUsers.createDent(),
                CSVUsers.createTrillian()
        );

        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("DoubleQuotes.csv"))
                .collect(Collectors.toList());

        assertEquals(expUsers.size(), userInputList.size());

        for (int i = 0; i < expUsers.size(); i++) {
            assertEquals(expUsers.get(i), userInputList.get(i));
        }
    }

    @Test
    public void testFieldLineBreaks() throws CsvRequiredFieldEmptyException {
        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("LineBreaks.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertTrue(userInputList.get(0).getMail().contains("\n"));
    }

    @Test
    public void testDoubleQuotesInField() throws CsvRequiredFieldEmptyException {
        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("DoubleQuotesField.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals("Arthur \"big\" Dent", userInputList.get(0).getDisplayname());
    }

    @Test
    public void testUmlauts() throws CsvRequiredFieldEmptyException {
        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("Umlauts.csv"))
                .collect(Collectors.toList());

        assertFalse(userInputList.isEmpty());
        assertEquals("Arthür Dänß", userInputList.get(0).getDisplayname());
    }

    @Test
    public void testEmptyGivenName() throws CsvRequiredFieldEmptyException {
        CSVUserDTO expUser = CSVUsers.createDent();
        expUser.setGivenname("");

        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("EmptyGivenName.csv"))
                .collect(Collectors.toList());

        assertEquals(expUser, userInputList.get(0));
    }

    @Test
    public void testParseBoolean() throws CsvRequiredFieldEmptyException {
        CSVUserDTO expUsers = CSVUsers.createDent();

        List<CSVUserDTO> userInputList = new CSVParserImpl()
                .parse(readTestFile("Boolean.csv"))
                .collect(Collectors.toList());

        assertEquals(5, userInputList.size());

        for(CSVUserDTO user: userInputList){
            assertEquals(expUsers, user);
        }
    }

    @Test(expected=CsvRequiredFieldEmptyException.class)
    public void testInvalidHeader() throws CsvRequiredFieldEmptyException {
        CSVParserImpl parser = new CSVParserImpl();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidHeader.csv"))
                .collect(Collectors.toList());

        assertTrue(userInputList.isEmpty());
    }

    @Test(expected=CsvRequiredFieldEmptyException.class)
    public void testInvalidFileType() throws CsvRequiredFieldEmptyException {
        CSVParserImpl parser = new CSVParserImpl();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("WrongFileType.txt"))
                .collect(Collectors.toList());

        assertTrue(userInputList.isEmpty());
    }



    @Test()
    public void testInvalidLineFieldLength() throws CsvRequiredFieldEmptyException {
        CSVParserImpl parser = new CSVParserImpl();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_NumberFields.csv"))
                .collect(Collectors.toList());

        assertEquals(3, userInputList.size());
        assertEquals(2, parser.getErrors().count());
    }

    @Test()
    public void testMissingRequiredField() throws CsvRequiredFieldEmptyException {
        CSVParserImpl parser = new CSVParserImpl();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_MissingRequiredField.csv"))
                .collect(Collectors.toList());

        assertEquals(1, userInputList.size());
        assertEquals(1, parser.getErrors().count());
    }

    @Test()
    public void testInvalidLineDelimiter() throws CsvRequiredFieldEmptyException {
        CSVParserImpl parser = new CSVParserImpl();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_Delimiter.csv"))
                .collect(Collectors.toList());

        assertEquals(2, userInputList.size());
        assertEquals(1, parser.getErrors().count());
    }

    @Test()
    public void testInvalidTypeConversion() throws CsvRequiredFieldEmptyException {
        CSVParserImpl parser = new CSVParserImpl();

        List<CSVUserDTO> userInputList = parser
                .parse(readTestFile("InvalidLine_TypeConversion.csv"))
                .collect(Collectors.toList());

        assertEquals(1, parser.getErrors().count());
    }


    private InputStreamReader readTestFile(String filename) {
        return new InputStreamReader(Objects.requireNonNull(readTestFileInputStream(filename)));
    }

    static InputStream readTestFileInputStream(String filename) {
        ClassLoader classLoader = CSVParserTest.class.getClassLoader();

        File file = Optional
                .ofNullable(classLoader.getResource("csvimports/" + filename))
                .map(URL::getFile)
                .map(File::new)
                .orElse(null);

        assertNotNull(file);

        try {
            return Files.newInputStream(file.toPath());
        } catch (IOException e) {
            fail("Unable to read test file: " + e.getMessage());

            return null;
        }
    }

}
