/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ssdorra
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
    Preconditions.checkArgument(username.equals(account.getUsername()));
    userManager.modify(account);
  }
  
}
