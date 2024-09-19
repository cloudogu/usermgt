package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class XmlCipherAdapterTest
{

  /**
   * Method description
   *
   */
  @Test
  public void testMarshallUnmarshall()
  {
    XmlCipherAdapter adapter = new XmlCipherAdapter();
    String encrypted = adapter.marshal("somevalue");

    assertNotEquals("somevalue", encrypted);

    String decrypted = adapter.unmarshal(encrypted);

    assertEquals("somevalue", decrypted);
  }
}
