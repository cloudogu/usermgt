/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.account;

import de.triology.universeadm.Resources;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.Users;
import java.io.IOException;
import java.net.URISyntaxException;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author ssdorra
 */
public class AccountResourceTest
{

  @Test
  public void testGetAccount() throws URISyntaxException, IOException
  {
    AccountManager am = mock(AccountManager.class);
    User user = Users.createDent();
    when(am.getCurrentUser()).thenReturn(user);
    MockHttpRequest request = MockHttpRequest.get("/account");
    MockHttpResponse response = Resources.dispatch(new AccountResource(am), request);
    assertEquals(200, response.getStatus());
  }
  
  @Test
  public void testGetAccountForbidden() throws URISyntaxException, IOException
  {
    AccountManager am = mock(AccountManager.class);
    MockHttpRequest request = MockHttpRequest.get("/account");
    MockHttpResponse response = Resources.dispatch(new AccountResource(am), request);
    assertEquals(403, response.getStatus());
  }
  
  @Test
  public void testModifyAccount() throws URISyntaxException, IOException
  {
    AccountManager am = mock(AccountManager.class);
    User user = Users.createDent();
    MockHttpRequest request = MockHttpRequest.put("/account");
    MockHttpResponse response = Resources.dispatch(new AccountResource(am), request, user);
    assertEquals(204, response.getStatus());
    verify(am, times(1)).modifyCurrentUser(user);
  }
}
