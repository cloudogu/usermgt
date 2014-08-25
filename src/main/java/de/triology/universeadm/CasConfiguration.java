/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */

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
