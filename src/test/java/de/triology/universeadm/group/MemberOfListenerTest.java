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

import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserEvent;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class MemberOfListenerTest
{
  
  @Mock
  private LDAPConnectionStrategy strategy;
  
  @Mock
  private GroupManager groupManager;

  private User user;
  
  private Group group;

  private MemberOfListener listener;
  
  @Before
  public void prepareForTest(){
    listener = new MemberOfListener(strategy, groupManager);
    user = new User("dent");
    group = new Group("Heart Of Gold");
    when(groupManager.get("Heart Of Gold")).thenReturn(group);
  }
  
  @Test
  public void testHandleUserCreateEvent()
  {
    user.getMemberOf().add("Heart Of Gold");
    listener.handleUserEvent(new UserEvent(user, EventType.CREATE));
    assertTrue(group.getMembers().contains("dent"));
    verify(groupManager, times(1)).modify(group, false);
  }
  
  @Test
  public void testHandleUserRemoveEvent()
  {
    user.getMemberOf().add("Heart Of Gold");
    group.getMembers().add("dent");
    listener.handleUserEvent(new UserEvent(user, EventType.REMOVE));
    assertFalse(group.getMembers().contains("dent"));
    verify(groupManager, times(1)).modify(group, false);
  }
  
  @Test
  public void testHandleUserModifyAddEvent()
  {
    User oldUser = new User("dent");
    user.getMemberOf().add("Heart Of Gold");
    listener.handleUserEvent(new UserEvent(user, oldUser));
    assertTrue(group.getMembers().contains("dent"));
    verify(groupManager, times(1)).modify(group, false);
  }
  
  @Test
  public void testHandleUserModifyRemoveEvent()
  {
    User oldUser = new User("dent");
    oldUser.getMemberOf().add("Heart Of Gold");
    group.getMembers().add("dent");
    listener.handleUserEvent(new UserEvent(user, oldUser));
    assertFalse(group.getMembers().contains("dent"));
    verify(groupManager, times(1)).modify(group, false);
  }
  
}
