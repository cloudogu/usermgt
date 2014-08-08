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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import de.triology.universeadm.EntityAlreadyExistsException;
import de.triology.universeadm.EntityNotFoundException;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.Resources;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class UserResourceTest
{

  private UserManager mockUserManager()
  {
    UserManager manager = mock(UserManager.class);
    User dent = Users.createDent();
    when(manager.get("dent")).thenReturn(dent);
    List<User> all = ImmutableList.of(dent);
    when(manager.getAll()).thenReturn(all);
    when(manager.getAll(0, 20)).thenReturn(new PagedResultList<>(all, 0, 20, 1));
    return manager;
  }

  @Test
  public void testGet() throws URISyntaxException, IOException
  {
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.get("/users/dent");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    JsonNode node = Resources.parseJson(response);
    assertEquals("dent", node.path("username").asText());
    assertEquals("arthur.dent@hitchhiker.com", node.path("mail").asText());
  }

  @Test
  public void testGetNotFound() throws URISyntaxException, IOException
  {
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.get("/users/trillian");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

  @Test
  public void testGetAll() throws URISyntaxException, IOException
  {
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.get("/users?start=0&limit=20");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    JsonNode node = Resources.parseJson(response);
    assertEquals(0, node.get("start").asInt());
    assertEquals(20, node.get("limit").asInt());
    assertEquals(1, node.get("totalEntries").asInt());
    JsonNode entries = node.get("entries");
    assertTrue(entries.isArray());
    assertEquals("dent", Iterables.get(entries, 0).path("username").asText());
  }

  @Test
  public void testGreate() throws URISyntaxException, IOException
  {
    User trillian = Users.createTrillian();
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.post("/users");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request, trillian);
    assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    URI location = (URI) response.getOutputHeaders().getFirst("Location");
    assertTrue(location.getPath().endsWith("users/trillian"));
    verify(manager).create(trillian);
  }

  @Test
  public void testCreateAlreadyExists() throws URISyntaxException, IOException
  {
    User dent = Users.createDent();
    UserManager manager = mockUserManager();
    doThrow(EntityAlreadyExistsException.class).when(manager).create(dent);
    MockHttpRequest request = MockHttpRequest.post("/users");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request, dent);
    assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
  }

  @Test
  public void testModify() throws URISyntaxException, IOException
  {
    User dent = Users.createDent();
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.put("/users/dent");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request, dent);
    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    verify(manager).modify(dent);
  }

  @Test
  public void testModifyNotFound() throws URISyntaxException, IOException
  {
    User trillian = Users.createTrillian();
    UserManager manager = mockUserManager();
    doThrow(EntityNotFoundException.class).when(manager).modify(trillian);
    MockHttpRequest request = MockHttpRequest.put("/users/trillian");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request, trillian);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    verify(manager).modify(trillian);
  }

  @Test
  public void testRemove() throws URISyntaxException, IOException
  {
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.delete("/users/dent");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request);
    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    verify(manager).remove(Users.createDent());
  }

  @Test
  public void testRemoveNotFound() throws URISyntaxException, IOException
  {
    UserManager manager = mockUserManager();
    MockHttpRequest request = MockHttpRequest.delete("/users/trillian");
    MockHttpResponse response = Resources.dispatch(new UserResource(manager), request);
    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

}
