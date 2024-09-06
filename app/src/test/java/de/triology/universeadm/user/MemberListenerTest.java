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

package de.triology.universeadm.user;

import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupEvent;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class MemberListenerTest
{
  @Mock 
  private LDAPConfiguration configuration;
          
  @Mock 
  private LDAPConnectionStrategy strategy;
  
  @Mock
  private UserManager userManager;
  
  private Group group;
  
  private User user;
  
  private MemberListener listener;
  
  @Before
  public void prepareTest(){
    listener = new MemberListener(configuration, strategy, userManager);
    user = new User("dent");
    group = new Group("Heart Of Gold");
    when(userManager.get("dent")).thenReturn(user);
  }
  
  @Test
  public void testGroupCreateEvent(){
    group.getMembers().add("dent");
    listener.handleGroupEvent(new GroupEvent(group, EventType.CREATE));
    assertTrue(user.getMemberOf().contains("Heart Of Gold"));
    verify(userManager).modify(user, false);
  }
  
  @Test
  public void testGroupRemoveEvent(){
    group.getMembers().add("dent");
    user.getMemberOf().add("Heart Of Gold");
    listener.handleGroupEvent(new GroupEvent(group, EventType.REMOVE));
    assertFalse(user.getMemberOf().contains("Heart Of Gold"));
    verify(userManager).modify(user, false);
  }
  
  @Test
  public void testGroupModifyAddEvent(){
    Group oldGroup = new Group(group.getName());
    group.getMembers().add("dent");
    listener.handleGroupEvent(new GroupEvent(group, oldGroup));
    assertTrue(user.getMemberOf().contains("Heart Of Gold"));
    verify(userManager).modify(user, false);
  }
  
  @Test
  public void testGroupModifyRemoveEvent(){
    Group oldGroup = new Group(group.getName());
    oldGroup.getMembers().add("dent");
    user.getMemberOf().add("Heart Of Gold");
    listener.handleGroupEvent(new GroupEvent(group, oldGroup));
    assertFalse(user.getMemberOf().contains("Heart Of Gold"));
    verify(userManager).modify(user, false);
  }
  
  @Test
  public void testDisabled(){
    when(configuration.isDisableMemberListener()).thenReturn(Boolean.TRUE);
    Group oldGroup = new Group(group.getName());
    group.getMembers().add("dent");
    listener.handleGroupEvent(new GroupEvent(group, oldGroup));
    verify(userManager, never()).modify(user, false);
  }
  
}
