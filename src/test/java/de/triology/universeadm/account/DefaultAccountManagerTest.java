/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.account;

import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import de.triology.universeadm.user.Users;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;
import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;

/**
 *
 * @author ssdorra
 */
@SubjectAware(configuration = "classpath:de/triology/universeadm/shiro.001.ini", username = "dent", password = "secret")
public class DefaultAccountManagerTest
{

  @Test
  public void testGetAccount()
  {
    UserManager um = mock(UserManager.class);
    when(um.get("dent")).thenReturn(Users.createDent());
    DefaultAccountManager am = new DefaultAccountManager(um);
    User user = am.getCurrentUser();
    assertNotNull(user);
    assertEquals("dent", user.getUsername());
  }
  
  @Test
  public void testModifyCurrentUser(){
    UserManager um = mock(UserManager.class);
    DefaultAccountManager am = new DefaultAccountManager(um);
    User dent = Users.createDent();
    am.modifyCurrentUser(dent);
    verify(um, times(1)).modify(dent);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testModifyOtherUser(){
    UserManager um = mock(UserManager.class);
    DefaultAccountManager am = new DefaultAccountManager(um);
    User trillian = Users.createTrillian();
    am.modifyCurrentUser(trillian);
  }  
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
}
