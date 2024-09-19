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
import org.mockito.junit.MockitoJUnitRunner;

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
