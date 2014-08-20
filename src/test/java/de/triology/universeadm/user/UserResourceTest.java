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
 * http://www.scm-userManager.com
 */



package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import de.triology.universeadm.EntityAlreadyExistsException;
import de.triology.universeadm.EntityNotFoundException;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.Resources;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.group.Groups;

import org.codehaus.jackson.JsonNode;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class UserResourceTest
{

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testAddMembership() throws URISyntaxException, IOException
  {
    MockHttpRequest request =
      MockHttpRequest.post("/users/dent/groups/heartOfGold");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testAddMembershipConflict() throws URISyntaxException, IOException
  {
    User trillian = Users.createTrillian();

    trillian.getMemberOf().add("heartOfGold");
    when(userManager.get("trillian")).thenReturn(trillian);

    MockHttpRequest request =
      MockHttpRequest.post("/users/trillian/groups/heartOfGold");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testAddMembershipGroupNotFound()
    throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.post("/users/dent/groups/towel");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testAddMembershipUserNotFound()
    throws URISyntaxException, IOException
  {
    MockHttpRequest request =
      MockHttpRequest.post("/users/slarti/groups/heartOfGold");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testCreateAlreadyExists() throws URISyntaxException, IOException
  {
    User dent = Users.createDent();

    doThrow(EntityAlreadyExistsException.class).when(userManager).create(dent);

    MockHttpRequest request = MockHttpRequest.post("/users");
    MockHttpResponse response = Resources.dispatch(resource, request, dent);

    assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testGet() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.get("/users/dent");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_OK, response.getStatus());

    JsonNode node = Resources.parseJson(response);

    assertEquals("dent", node.path("username").asText());
    assertEquals("arthur.dent@hitchhiker.com", node.path("mail").asText());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testGetAll() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.get("/users?start=0&limit=20");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_OK, response.getStatus());

    JsonNode node = Resources.parseJson(response);

    assertEquals(0, node.get("start").asInt());
    assertEquals(20, node.get("limit").asInt());
    assertEquals(1, node.get("totalEntries").asInt());

    JsonNode entries = node.get("entries");

    assertTrue(entries.isArray());
    assertEquals("dent", Iterables.get(entries, 0).path("username").asText());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testGetNotFound() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.get("/users/trillian");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testGreate() throws URISyntaxException, IOException
  {
    User trillian = Users.createTrillian();
    MockHttpRequest request = MockHttpRequest.post("/users");
    MockHttpResponse response = Resources.dispatch(resource, request, trillian);

    assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

    URI location = (URI) response.getOutputHeaders().getFirst("Location");

    assertTrue(location.getPath().endsWith("users/trillian"));
    verify(userManager).create(trillian);
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testModify() throws URISyntaxException, IOException
  {
    User dent = Users.createDent();
    MockHttpRequest request = MockHttpRequest.put("/users/dent");
    MockHttpResponse response = Resources.dispatch(resource, request, dent);

    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    verify(userManager).modify(dent);
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testModifyNotFound() throws URISyntaxException, IOException
  {
    User trillian = Users.createTrillian();

    doThrow(EntityNotFoundException.class).when(userManager).modify(trillian);

    MockHttpRequest request = MockHttpRequest.put("/users/trillian");
    MockHttpResponse response = Resources.dispatch(resource, request, trillian);

    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    verify(userManager).modify(trillian);
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testRemove() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.delete("/users/dent");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    verify(userManager).remove(Users.createDent());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testRemoveNotFound() throws URISyntaxException, IOException
  {
    MockHttpRequest request = MockHttpRequest.delete("/users/trillian");
    MockHttpResponse response = Resources.dispatch(resource, request);

    assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   */
  @Before
  public void setUp()
  {
    this.userManager = mockUserManager();
    this.groupManager = mockGroupManager();
    this.resource = new UserResource(userManager, groupManager);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  private GroupManager mockGroupManager()
  {
    GroupManager manager = mock(GroupManager.class);

    when(manager.get("heartOfGold")).thenReturn(Groups.createHeartOfGold());

    return manager;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private UserManager mockUserManager()
  {
    UserManager manager = mock(UserManager.class);
    User dent = Users.createDent();

    when(manager.get("dent")).thenReturn(dent);

    List<User> all = ImmutableList.of(dent);

    when(manager.getAll()).thenReturn(all);
    when(manager.getAll(0, 20)).thenReturn(new PagedResultList<>(all, 0, 20,
      1));

    return manager;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private GroupManager groupManager;

  /** Field description */
  private UserResource resource;

  /** Field description */
  private UserManager userManager;
}
