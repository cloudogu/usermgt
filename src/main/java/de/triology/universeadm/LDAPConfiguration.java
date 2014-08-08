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
  @XmlJavaTypeAdapter(XmlCipherAdapter.class)
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
