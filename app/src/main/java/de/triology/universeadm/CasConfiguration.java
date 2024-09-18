package de.triology.universeadm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@XmlRootElement(name = "cas")
@XmlAccessorType(XmlAccessType.FIELD)
public class CasConfiguration
{
  public static final String FILE = "cas.xml";
  
  @XmlElement(name = "server-url")
  private String serverUrl;
  
  @XmlElement(name = "login-url")
  private String loginUrl;
  
  @XmlElement(name = "failure-url")
  private String failureUrl;
  
  private String service;
  
  @XmlElement(name = "role-attribute-names")
  private String roleAttributeNames;
  
  @XmlElement(name = "logout-url")
  private String logoutUrl;
  
  @XmlElement(name = "administrator-role")
  private String administratorRole;

  public String getFailureUrl()
  {
    return failureUrl;
  }
  
  public String getAdministratorRole()
  {
    return administratorRole;
  }

  public String getServerUrl()
  {
    return serverUrl;
  }

  public String getLoginUrl()
  {
    return loginUrl;
  }

  public String getService()
  {
    return service;
  }

  public String getRoleAttributeNames()
  {
    return roleAttributeNames;
  }
  
  public String getLogoutUrl()
  {
    return logoutUrl;
  }
  
}
