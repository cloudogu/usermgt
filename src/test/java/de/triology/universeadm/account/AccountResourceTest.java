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
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
