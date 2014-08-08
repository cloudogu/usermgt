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

package de.triology.universeadm.group;

import com.github.legman.Subscribe;
import com.google.common.collect.Collections2;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.DoesNotContainPredicate;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserEvent;
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
public final class MemberOfListener
{

  private static final Logger logger = LoggerFactory.getLogger(MemberOfListener.class);

  private final LDAPConnectionStrategy strategy;
  private final GroupManager groupManager;

  @Inject
  public MemberOfListener(LDAPConnectionStrategy strategy, GroupManager groupManager)
  {
    this.strategy = strategy;
    this.groupManager = groupManager;
  }

  @Subscribe
  public void handleUserEvent(UserEvent event)
  {
    logger.trace("handle user event {}", event);
    // we are asynchronous, so we have to bind and release the connection 
    // strategy manually. TODO find a better automatically way.
    strategy.bind();
    try
    {
      doHandleUserEvent(event);
    }
    finally
    {
      strategy.release();
    }
  }

  private void doHandleUserEvent(UserEvent event)
  {
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

  private void handleCreate(User user)
  {
    addUserToGroups(user.getUsername(), user.getMemberOf());
  }
  
  private void handleModify(User user, User oldUser)
  {
    List<String> groups = user.getMemberOf();
    List<String> oldGroups = oldUser.getMemberOf();
    
    Collection<String> removed = Collections2.filter(
       oldGroups, new DoesNotContainPredicate<>(groups)
    );
    
    addUserToGroups(user.getUsername(), groups);
    removeUserFromGroups(user.getUsername(), removed);
  }

  private void handleRemove(User user)
  {
    removeUserFromGroups(user.getUsername(), user.getMemberOf());
  }
  
  private void removeUserFromGroups(String member, Iterable<String> groupnames)
  {
    for (String groupname : groupnames)
    {
      removeUserFromGroup(member, groupname);
    }
  }
  
  private void removeUserFromGroup(String member, String groupname)
  {
    Group group = groupManager.get(groupname);
    if (group != null)
    {
      if (group.getMembers().contains(member))
      {
        group.getMembers().remove(member);
        modify(group);
      }
      else
      {
        logger.debug("group {} does not contain member {}", groupname, member);
      }
    }
    else
    {
      logger.warn("group {} does not exists", groupname);
    }
  }
  
  private void addUserToGroups(String member, Iterable<String> groupnames)
  {
    for (String groupname : groupnames)
    {
      addUserToGroup(member, groupname);
    }
  }
  
  private void addUserToGroup(String member, String groupname)
  {
    Group group = groupManager.get(groupname);
    if (group != null)
    {
      if (!group.getMembers().contains(member))
      {
        group.getMembers().add(member);
        modify(group);
      }
      else
      {
        logger.debug("group {} already contains member {}", groupname, member);
      }
    }
    else
    {
      logger.warn("group {} does not exists", groupname);
    }
  }
  
  private void modify(Group group){
    try 
    {
      // do not send events, because this would execute the checks twice
      groupManager.modify(group, false);
    } 
    catch ( ConstraintViolationException ex )
    {
      logger.warn("could not modify group {}, because the group is not valid", group.getName());
    }
  }

}
