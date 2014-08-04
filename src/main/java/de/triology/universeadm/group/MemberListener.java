/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.group;

import com.github.legman.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public final class MemberListener
{

  private static final Logger logger = LoggerFactory.getLogger(MemberListener.class);

  private final GroupManager groupManager;

  @Inject
  public MemberListener(GroupManager groupManager)
  {
    this.groupManager = groupManager;
  }
  
  @Subscribe
  public void handleUserEvent(UserEvent event)
  {
    logger.trace("handle user event {}", event);
    switch (event.getType())
    {
      case CREATE:
        handleCreate(event.getEntity());
        break;
      case MODIFY:
        handleModify(event.getEntity(), event.getOldEntity());
        break;
      case REMOVE:
        handleRemove(event.getEntity());
        break;
    }
  }
  
  private void handleCreate(User user){
    
  }
  
  private void handleModify(User user, User oldUser){
  }

  private void handleRemove(User user){
  }
  
}
