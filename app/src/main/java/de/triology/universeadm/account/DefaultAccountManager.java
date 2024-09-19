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


  public void modifyCurrentUser(User account) {
    String username = getSessionUser();
    logger.trace("try to modify account {} as user {}", account.getUsername(), username);
    Preconditions.checkArgument(username.equals(account.getUsername()), "the username must not be changed");
    userManager.modify(account);
  }
  
}
