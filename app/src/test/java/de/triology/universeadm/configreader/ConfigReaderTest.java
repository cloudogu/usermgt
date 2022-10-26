package de.triology.universeadm.configreader;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigReaderTest {
    private JsonConfigReader reader;

    @Before
    public void setUP() {
        reader = new JsonConfigReader("src/test/java/de/triology/universeadm/configreader/config.json");
    }

    @Test
    public void getKeyWhichDoesExist() {
        String test = reader.get("testcheck");
        assertEquals("testcheck", test);
    }

    @Test
    public void getKeyWhichDoesNotExist() {
        String test = reader.get("testfailure");
        assertEquals(null, test);
    }
}
