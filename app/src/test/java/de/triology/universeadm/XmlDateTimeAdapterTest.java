package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class XmlDateTimeAdapterTest
{

  /**
   * Method description
   *
   */
  @Test
  public void testMarshall()
  {
    XmlDateTimeAdapter adapter = new XmlDateTimeAdapter();
    DateTime dt = new DateTime(2014, 8, 27, 8, 43, DateTimeZone.UTC);

    assertEquals("2014-08-27T08:43:00.000Z", adapter.marshal(dt));
  }

  /**
   * Method description
   *
   */
  @Test
  public void testUnmarshall()
  {
    XmlDateTimeAdapter adapter = new XmlDateTimeAdapter();
    DateTime dt = new DateTime(2014, 8, 27, 8, 43, DateTimeZone.UTC);

    assertEquals(dt, adapter.unmarshal("2014-08-27T08:43:00.000Z"));
  }
}
