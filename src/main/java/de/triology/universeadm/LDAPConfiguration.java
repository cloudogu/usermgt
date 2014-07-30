/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

//~--- JDK imports ------------------------------------------------------------
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra
 */
@XmlRootElement(name = "ldap")
@XmlAccessorType(XmlAccessType.FIELD)
public class LDAPConfiguration
{

  /**
   * Constructs ...
   *
   */
  public LDAPConfiguration()
  {
  }

  public LDAPConfiguration(String host, int port, String bindDN, String bindPassword, String userBaseDN, String groupBaseDN)
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

  public String getGroupBaseDN()
  {
    return groupBaseDN;
  }

  public String getPasswordAlgorithm()
  {
    return passwordAlgorithm;
  }

  public boolean isRequirePreEncodedPasswords()
  {
    return requirePreEncodedPasswords;
  }

  //~--- fields ---------------------------------------------------------------
  @XmlElement(name = "password-algorithm")
  private String passwordAlgorithm = "SSHA";

  /**
   * Field description
   */
  @XmlElement(name = "bind-dn")
  private String bindDN;

  /**
   * Field description
   */
  @XmlElement(name = "bind-password")
  private String bindPassword;

  /**
   * Field description
   */
  private String host;

  /**
   * Field description
   */
  private int port;

  @XmlElement(name = "require-pre-encoded-passwords")
  private boolean requirePreEncodedPasswords = true;

  /**
   * Field description
   */
  @XmlElement(name = "user-base-dn")
  private String userBaseDN = "ou=People";

  @XmlElement(name = "group-base-dn")
  private String groupBaseDN = "ou=Groups";
}
