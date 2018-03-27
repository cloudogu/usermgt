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

package de.triology.universeadm.account;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultAccountManager implements AccountManager
{
  
  private static final Logger logger = LoggerFactory.getLogger(DefaultAccountManager.class);

  private final UserManager userManager;
  
  @Inject
  public DefaultAccountManager(UserManager userManager)
  {
    this.userManager = userManager;
  }

  @Override
  public User getCurrentUser()
  {
    String username = getSessionUser();
    logger.trace("get current user {} from user manager", username);
    return userManager.get(username);
  }
  
  private String getSessionUser(){
    Subject subject = SecurityUtils.getSubject();
    return subject.getPrincipal().toString();
  }

  @Override
  public void modifyCurrentUser(User account)
  {
    String username = getSessionUser();
    logger.trace("try to modify account {} as user {}", account.getUsername(), username);
    Preconditions.checkArgument(username.equals(account.getUsername()));
    userManager.modify(account);
  }
  
}
