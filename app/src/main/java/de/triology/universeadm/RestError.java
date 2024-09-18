package de.triology.universeadm;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestError
{

  /**
   * Constructs ...
   *
   */
  public RestError() {}

  /**
   * Constructs ...
   *
   *
   * @param message
   */
  public RestError(String message)
  {
    this.message = message;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getMessage()
  {
    return message;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String message;
}
