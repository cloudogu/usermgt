package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class XmlDateTimeAdapter extends XmlAdapter<String, DateTime>
{

  /** Field description */
  private static final DateTimeFormatter ISO8601 =
    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param v
   *
   * @return
   */
  @Override
  public String marshal(DateTime v)
  {
    return ISO8601.print(v);
  }

  /**
   * Method description
   *
   *
   * @param v
   *
   * @return
   */
  @Override
  public DateTime unmarshal(String v)
  {
    return ISO8601.parseDateTime(v);
  }
}
