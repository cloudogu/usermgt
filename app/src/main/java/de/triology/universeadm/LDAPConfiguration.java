package de.triology.universeadm;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@XmlRootElement(name = "ldap")
@XmlAccessorType(XmlAccessType.FIELD)
public class LDAPConfiguration
{

  /**
   * Constructs ...
   *
   */
  public LDAPConfiguration() {}

  /**
   * Constructs ...
   *
   *
   * @param host
   * @param port
   * @param bindDN
   * @param bindPassword
   * @param userBaseDN
   * @param groupBaseDN
   */
  public LDAPConfiguration(String host, int port, String bindDN,
    String bindPassword, String userBaseDN, String groupBaseDN)
  {
    this.host = host;
    this.port = port;
    this.bindDN = bindDN;
    this.bindPassword = bindPassword;
    this.userBaseDN = userBaseDN;
    this.groupBaseDN = groupBaseDN;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getBindDN()
  {
    return bindDN;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getBindPassword()
  {
    return bindPassword;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getGroupBaseDN()
  {
    return groupBaseDN;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getHost()
  {
    return host;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getPasswordAlgorithm()
  {
    return passwordAlgorithm;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public int getPort()
  {
    return port;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUserBaseDN()
  {
    return userBaseDN;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isDisabled()
  {
    return disabled;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public boolean isRequirePreEncodedPasswords()
  {
    return requirePreEncodedPasswords;
  }

  public boolean isDisableMemberListener() 
  {
    return disableMemberListener;
  }

  //~--- fields ---------------------------------------------------------------

  @XmlElement(name = "disable-member-listener")
  private boolean disableMemberListener = false;
  
  /**
   * Field description
   */
  @XmlElement(name = "bind-dn")
  private String bindDN;

  /**
   * Field description
   */
  @XmlElement(name = "bind-password")
  @XmlJavaTypeAdapter(XmlCipherAdapter.class)
  private String bindPassword;

  /** Field description */
  @XmlElement(name = "group-base-dn")
  private String groupBaseDN = "ou=Groups";

  /**
   * Field description
   */
  private String host;

  /** Field description */
  @XmlElement(name = "password-algorithm")
  private String passwordAlgorithm = "SSHA";

  /**
   * Field description
   */
  private int port;

  /** Field description */
  @XmlElement(name = "require-pre-encoded-passwords")
  private boolean requirePreEncodedPasswords = true;

  /** Field description */
  private boolean disabled = false;

  /**
   * Field description
   */
  @XmlElement(name = "user-base-dn")
  private String userBaseDN = "ou=People";
}
