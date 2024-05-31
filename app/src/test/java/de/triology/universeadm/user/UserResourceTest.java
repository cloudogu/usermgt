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

import com.github.sdorra.shiro.SubjectAware;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.triology.universeadm.*;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.group.Groups;
import de.triology.universeadm.user.imports.*;
import org.apache.shiro.authz.AuthorizationException;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.artifact.InvalidArgumentException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
        configuration = "classpath:de/triology/universeadm/shiro.001.ini",
        username = "trillian",
        password = "secret"
)
public class UserResourceTest {
    private GroupManager groupManager;
    private UserResource resource;
    private UserManager userManager;
    private ImportHandler importHandler;

    @Test
    public void testAddMembership() throws URISyntaxException, IOException {
        MockHttpRequest request =
                MockHttpRequest.post("/users/dent/groups/heartOfGold");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
    }


    @Test
    public void testAddMembershipConflict() throws URISyntaxException, IOException {
        User trillian = Users.createTrillian();

        trillian.getMemberOf().add("heartOfGold");
        when(userManager.get("trillian")).thenReturn(trillian);

        MockHttpRequest request =
                MockHttpRequest.post("/users/trillian/groups/heartOfGold");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }

    @Test
    public void testAddMembershipGroupNotFound()
            throws URISyntaxException, IOException {
        MockHttpRequest request = MockHttpRequest.post("/users/dent/groups/towel");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void testAddMembershipUserNotFound()
            throws URISyntaxException, IOException {
        MockHttpRequest request =
                MockHttpRequest.post("/users/slarti/groups/heartOfGold");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void testCreateAlreadyExists() throws URISyntaxException, IOException {
        User dent = Users.createDent();

        doThrow(new UniqueConstraintViolationException(Constraint.ID.UNIQUE_USERNAME)).when(userManager).create(dent);

        MockHttpRequest request = MockHttpRequest.post("/users");
        MockHttpResponse response = Resources.dispatch(resource, request, dent);

        assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
    }

    @Test
    public void testGet() throws URISyntaxException, IOException {
        MockHttpRequest request = MockHttpRequest.get("/users/dent");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        JsonNode node = Resources.parseJson(response);

        assertEquals("dent", node.path("username").asText());
        assertEquals("arthur.dent@hitchhiker24.com", node.path("mail").asText());
    }

    @Test
    public void testGetAll() throws URISyntaxException, IOException {
        MockHttpRequest request = MockHttpRequest.get("/users?page=1&page_size=20");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        JsonNode node = Resources.parseJson(response);

        JsonNode meta = node.get("meta");
        assertNotNull(meta);
        assertEquals(1, meta.get("page").asInt());
        assertEquals(20, meta.get("pageSize").asInt());
        assertEquals(1, meta.get("totalPages").asInt());
        assertEquals(1, meta.get("totalItems").asInt());

        JsonNode links = node.get("links");
        assertNotNull(links);
        assertEquals("/users?page_size=20&sort_by=username&page=1", links.get("self").getTextValue());
        assertEquals("/users?page_size=20&sort_by=username&page=1", links.get("first").getTextValue());
        assertNull(links.get("prev").getTextValue());
        assertNull(links.get("next").getTextValue());
        assertEquals("/users?page_size=20&sort_by=username&page=1", links.get("last").getTextValue());

        JsonNode data = node.get("data");

        assertTrue(data.isArray());
        assertEquals("dent", Iterables.get(data, 0).path("username").asText());
    }

    @Test
    public void testGetNotFound() throws URISyntaxException, IOException {
        MockHttpRequest request = MockHttpRequest.get("/users/trillian");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void testGreate() throws URISyntaxException, IOException {
        User trillian = Users.createTrillian();
        MockHttpRequest request = MockHttpRequest.post("/users");
        MockHttpResponse response = Resources.dispatch(resource, request, trillian);

        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        URI location = (URI) response.getOutputHeaders().getFirst("Location");

        assertTrue(location.getPath().endsWith("users/tricia"));
        verify(userManager).create(trillian);
    }

    @Test
    public void testModify() throws URISyntaxException, IOException {
        User dent = Users.createDent();
        MockHttpRequest request = MockHttpRequest.put("/users/dent");
        MockHttpResponse response = Resources.dispatch(resource, request, dent);

        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        verify(userManager).modify(dent);
    }

    @Test
    public void testModifyNotFound() throws URISyntaxException, IOException {
        User trillian = Users.createTrillian();

        doThrow(EntityNotFoundException.class).when(userManager).modify(trillian);

        MockHttpRequest request = MockHttpRequest.put("/users/tricia");
        MockHttpResponse response = Resources.dispatch(resource, request, trillian);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        verify(userManager).modify(trillian);
    }

    @Test
    public void testRemove() throws URISyntaxException, IOException {
        MockHttpRequest request = MockHttpRequest.delete("/users/dent");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
        verify(userManager).remove(Users.createDent());
    }

    @Test
    public void testRemoveNotFound() throws URISyntaxException, IOException {
        MockHttpRequest request = MockHttpRequest.delete("/users/trillian");
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
    }

    @Test
    public void testImport() throws CsvRequiredFieldEmptyException, IOException {
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        UserManager userManager = mock(UserManager.class);
        GroupManager groupManager = mock(GroupManager.class);

        ImportHandler importHandler = mock(ImportHandler.class);
        when(importHandler.handle(input)).thenReturn(new Result(
                UUID.randomUUID(),
                "testFile.csv",
                Collections.singletonList(Users.createDent()),
                Collections.singletonList(Users.createDent2()),
                Collections.singletonList(new ImportError.Builder(ImportError.Code.UNIQUE_FIELD_ERROR)
                        .withLineNumber(3)
                        .withErrorMessage("testError")
                        .build())));

        this.resource = new UserResource(userManager, groupManager, importHandler);
        Response response = this.resource.importUsers(input);

        assertNotNull(response);
        verify(importHandler, times(1)).handle(input);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testImportParsingHeaderError() throws NoSuchFieldException, CsvRequiredFieldEmptyException, IOException {
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        UserManager userManager = mock(UserManager.class);
        GroupManager groupManager = mock(GroupManager.class);

        ImportHandler importHandler = mock(ImportHandler.class);
        doThrow(new CsvRequiredFieldEmptyException(
            CSVParser.class,
            CSVUserDTO.class.getDeclaredField("username"),
            "Error"
        )).when(importHandler).handle(input);

        this.resource = new UserResource(userManager, groupManager, importHandler);
        Response response = this.resource.importUsers(input);

        assertNotNull(response);
        verify(importHandler, times(1)).handle(input);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ImportError expError = new ImportError.Builder(ImportError.Code.MISSING_FIELD_ERROR)
                .withLineNumber(0)
                .withErrorMessage("Error")
                .withAffectedColumns(Collections.singletonList("username"))
                .build();

        ImportError importError = response.readEntity(ImportError.class);
        assertEquals(expError, importError);
    }

    @Test
    public void testImportInvalidRequest() throws CsvRequiredFieldEmptyException, IOException {
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        UserManager userManager = mock(UserManager.class);
        GroupManager groupManager = mock(GroupManager.class);

        ImportHandler importHandler = mock(ImportHandler.class);
        doThrow(new InvalidArgumentException("Error")).when(importHandler).handle(input);

        this.resource = new UserResource(userManager, groupManager, importHandler);
        Response response = this.resource.importUsers(input);

        assertNotNull(response);
        verify(importHandler, times(1)).handle(input);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

        ImportError expError = new ImportError.Builder(ImportError.Code.MISSING_FIELD_ERROR)
                .withErrorMessage("Error")
                .build();

        ImportError importError = response.readEntity(ImportError.class);
        assertEquals(expError, importError);
    }

    @Test
    public void testImportForbidden() throws CsvRequiredFieldEmptyException, IOException {
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        UserManager userManager = mock(UserManager.class);
        GroupManager groupManager = mock(GroupManager.class);

        ImportHandler importHandler = mock(ImportHandler.class);
        doThrow(new AuthorizationException("Error")).when(importHandler).handle(input);

        this.resource = new UserResource(userManager, groupManager, importHandler);
        Response response = this.resource.importUsers(input);

        assertNotNull(response);
        verify(importHandler, times(1)).handle(input);
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void testImportInternalError() throws CsvRequiredFieldEmptyException, IOException {
        MultipartFormDataInput input = mock(MultipartFormDataInput.class);
        UserManager userManager = mock(UserManager.class);
        GroupManager groupManager = mock(GroupManager.class);

        ImportHandler importHandler = mock(ImportHandler.class);
        doThrow(new RuntimeException("Error")).when(importHandler).handle(input);

        this.resource = new UserResource(userManager, groupManager, importHandler);
        Response response = this.resource.importUsers(input);

        assertNotNull(response);
        verify(importHandler, times(1)).handle(input);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }


    //~--- set methods ----------------------------------------------------------

    @Before
    public void setUp() {
        this.userManager = mockUserManager();
        this.groupManager = mockGroupManager();
        this.importHandler = mock(ImportHandler.class);

        this.resource = new UserResource(userManager, groupManager, importHandler);
    }

    //~--- methods --------------------------------------------------------------

    private GroupManager mockGroupManager() {
        GroupManager manager = mock(GroupManager.class);

        when(manager.get("heartOfGold")).thenReturn(Groups.createHeartOfGold());

        return manager;
    }

    private UserManager mockUserManager() {
        UserManager manager = mock(UserManager.class);
        User dent = Users.createDent();

        when(manager.get("dent")).thenReturn(dent);

        List<User> all = ImmutableList.of(dent);

        when(manager.query(new PaginationQuery(1, 20, null, null, null, "username", false))).thenReturn(new PaginationResult<>(all, 1, null));

        return manager;
    }

    @BeforeClass
    public static void beforeClass()
    {
        System.setProperty("universeadm.home", "src/test/resources/");
    }
}
