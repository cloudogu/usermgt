package de.triology.universeadm.user;

import com.github.legman.Subscribe;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.DoesNotContainPredicate;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupEvent;
import java.util.Collection;
import java.util.List;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class MemberListener
{

  private static final Logger logger = LoggerFactory.getLogger(MemberListener.class);

  private final LDAPConfiguration configuration;
  private final LDAPConnectionStrategy strategy;
  private final UserManager userManger;

  @Inject
  public MemberListener(LDAPConfiguration configuration, LDAPConnectionStrategy strategy, UserManager userManger)
  {
    this.configuration = configuration;
    this.strategy = strategy;
    this.userManger = userManger;
  }

  @Subscribe
  public void handleGroupEvent(GroupEvent groupEvent)
  {
    if (!configuration.isDisableMemberListener())
    {
      logger.debug("execute member listener");
      
      // we are asynchronous, so we have to bind and release the connection 
      // strategy manually. TODO find a better automatically way.
      strategy.bind();
      try
      {
        doHandleGroupEvent(groupEvent);
      }
      finally
      {
        strategy.release();
      }
    }
    else
    {
      logger.debug("member listener is disabled");
    }
  }

  private void doHandleGroupEvent(GroupEvent groupEvent)
  {
    Group group = groupEvent.getEntity();
    switch (groupEvent.getType())
    {
      case CREATE:
        addGroupToUsers(group.getName(), group.getMembers());
        break;
      case MODIFY:
        handleModifyEvent(group, groupEvent.getOldEntity());
        break;
      case REMOVE:
        removeGroupFromUsers(group.getName(), group.getMembers());
        break;
    }
  }
  
  private void handleModifyEvent(Group group, Group oldGroup)
  {
    List<String> members = group.getMembers();
    List<String> oldMembers = oldGroup.getMembers();
    
    Collection<String> removed = Collections2.filter(
       oldMembers, new DoesNotContainPredicate<>(members)
    );
    
    addGroupToUsers(group.getName(), members);
    removeGroupFromUsers(group.getName(), removed);
  }

  private void removeGroupFromUsers(String groupname, Iterable<String> usernames){
    for ( String username : usernames ){
      removeGroupFromUser(groupname, username);
    }
  }
  
  private void removeGroupFromUser(String groupname, String username){
    User user = userManger.get(username);
    if (user != null)
    {
      if (user.getMemberOf().contains(groupname))
      {
        user.getMemberOf().remove(groupname);
        modify(user);
      }
      else
      {
        logger.debug("user {} does contain group {}", username, groupname);
      }
    }
    else
    {
      logger.warn("user {} does not exists", username);
    }
  }
  
  private void addGroupToUsers(String groupname, Iterable<String> usernames)
  {
    for (String username : usernames)
    {
      addGroupToUser(groupname, username);
    }
  }

  private void addGroupToUser(String groupname, String username)
  {
    User user = userManger.get(username);
    if (user != null)
    {
      if (!user.getMemberOf().contains(groupname))
      {
        user.getMemberOf().add(groupname);
        modify(user);
      }
      else
      {
        logger.debug("user {} does already contain group {}", username, groupname);
      }
    }
    else
    {
      logger.warn("user {} does not exists", username);
    }
  }
  
  private void modify(User user){
    try {
      userManger.modify(user, false);
    } catch ( ConstraintViolationException ex ){
      logger.warn("could not modify user {}, because the user is not valid", user.getUsername());
    }
  }

}
