package de.triology.universeadm;

import de.triology.universeadm.configuration.ApplicationConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.DataBindingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BaseDirectoryTest {

    @BeforeClass
    public static void beforeClass()
    {
        System.setProperty("universeadm.home", "src/test/resources/");
    }

    @Test
    public void testGetConfiguration(){
        assertEquals(System.getProperty("universeadm.home"), "src/test/resources/");

        ApplicationConfiguration config = BaseDirectory.getConfiguration("application-configuration.xml", ApplicationConfiguration.class);

        assertNotNull(config);
        assertEquals(config.getImportMailSubject(), "Ihr neues Kennwort");
    }

    @Test(expected = DataBindingException.class)
    public void testGetConfigurationFailsWithException(){
        assertEquals(System.getProperty("universeadm.home"), "src/test/resources/");

        BaseDirectory.getConfiguration("broken-application-configuration.xml", ApplicationConfiguration.class);

    }
}
