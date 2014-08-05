/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.user;

import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupEvent;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ssdorra
 */
@RunWith(MockitoJUnitRunner.class)
public class MemberListenerTest
{
  
  @Mock
  private LDAPConnectionStrategy strategy;
  
  @Mock
  private UserManager userManager;
  
  private Group group;
  
  private User user;
  
  private MemberListener listener;
  
  @Before
  public void prepareTest(){
    listener = new MemberListener(strategy, userManager);
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
  
}
