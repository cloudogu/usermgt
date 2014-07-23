/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ssdorra
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
  
  private String service;
  
  @XmlElement(name = "role-attribute-names")
  private String roleAttributeNames;

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
  
}
