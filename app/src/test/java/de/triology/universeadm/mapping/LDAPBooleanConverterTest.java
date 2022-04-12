package de.triology.universeadm.mapping;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LDAPBooleanConverterTest {

    @Test
    public void testEncodeTrue () {
        MappingEncoder encoder = new LDAPBooleanConverter();

        assertEquals("TRUE", encoder.encodeAsString(true));
    }

    @Test
    public void testEncodeFalse () {
        MappingEncoder encoder = new LDAPBooleanConverter();

        assertEquals("FALSE", encoder.encodeAsString(false));
    }

    @Test
    public void testEncodeNonBoolean () {
        MappingEncoder encoder = new LDAPBooleanConverter();

        assertEquals("FALSE", encoder.encodeAsString(new Object()));
    }

    @Test
    public void testDecodeTRUE () {
        MappingDecoder decoder = new LDAPBooleanConverter();

        assertEquals(true, decoder.decodeFromString(null, "TRUE"));
    }

    @Test
    public void testDecodeFALSE () {
        MappingDecoder decoder = new LDAPBooleanConverter();

        assertEquals(false, decoder.decodeFromString(null, "FALSE"));
    }
}