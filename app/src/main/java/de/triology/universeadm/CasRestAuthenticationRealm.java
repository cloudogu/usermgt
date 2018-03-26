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

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cas.CasAuthenticationException;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.CollectionUtils;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Saml11TicketValidator;
import org.jasig.cas.client.validation.TicketValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;

/**
 * Cas realm which uses the rest api of cas. This realm is required for the 
 * basic authentication against the api.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class CasRestAuthenticationRealm extends AuthenticatingRealm
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(CasRestAuthenticationRealm.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs new cas rest realm
   *
   *
   * @param casConfiguration cas configuration
   */
  @Inject
  public CasRestAuthenticationRealm(CasConfiguration casConfiguration)
  {
    setAuthenticationTokenClass(UsernamePasswordToken.class);
    setCredentialsMatcher(new AllowAllCredentialsMatcher());
    this.casConfiguration = casConfiguration;
    this.restClient = new CasRestClient(casConfiguration.getServerUrl(),
      casConfiguration.getService());
    this.ticketValidator =
      new Saml11TicketValidator(casConfiguration.getServerUrl());
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Return authentication info, if the authentication was successfully.
   *
   *
   * @param token username and password token
   *
   * @return authentication info
   */
  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(
    AuthenticationToken token)
  {
    if (!(token instanceof UsernamePasswordToken))
    {
      throw new IllegalStateException("username password token is required");
    }

    UsernamePasswordToken upt = (UsernamePasswordToken) token;

    String ticket = restClient.createServiceTicket(upt.getUsername(),
                      new String(upt.getPassword()));
    AttributePrincipal principal = validate(ticket);
    String username = principal.getName();

    logger.debug("successfully validated user {}", username);

    return createAuthenticationInfo(principal, username);
  }

  /**
   * Create authentication info from returned cas attributes.
   *
   *
   * @param principal cas principal
   * @param username username
   *
   * @return authentication info
   */
  private AuthenticationInfo createAuthenticationInfo(
    AttributePrincipal principal, String username)
  {
    Map<String, Object> attributes = principal.getAttributes();
    List<Object> principals = CollectionUtils.asList(username, attributes);
    PrincipalCollection principalCollection =
      new SimplePrincipalCollection(principals, getName());
    SimpleAuthenticationInfo info = new SimpleAuthenticationInfo();

    info.setPrincipals(principalCollection);

    return info;
  }

  /**
   * Validate the returned service ticket.
   *
   *
   * @param ticket service ticket
   *
   * @return validated attributes
   */
  private AttributePrincipal validate(String ticket)
  {
    try
    {
      return ticketValidator.validate(ticket,
        casConfiguration.getService()).getPrincipal();
    }
    catch (TicketValidationException ex)
    {
      throw new CasAuthenticationException("could not validate ticket", ex);
    }
  }

  //~--- fields ---------------------------------------------------------------

  /** cas configuration */
  private final CasConfiguration casConfiguration;

  /** rest client */
  private final CasRestClient restClient;

  /** saml ticket validator */
  private final Saml11TicketValidator ticketValidator;
}
