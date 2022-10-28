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
import de.triology.universeadm.*;
import de.triology.universeadm.configreader.ApplicationConfigReader;
import de.triology.universeadm.configreader.ApplicationConfiguration;
import de.triology.universeadm.configreader.LanguageConfigReader;
import de.triology.universeadm.configreader.LanguageConfiguration;
import de.triology.universeadm.csvimport.CSVImportManager;
import de.triology.universeadm.csvimport.ProtocolWriter;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.group.Groups;
import de.triology.universeadm.mail.MailSender;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    private CSVImportManager csvImportManager;

    private UserResource resource;

    private UserManager userManager;

    private ProtocolWriter protocolWriter;

    private MailSender mailSender;

    private LanguageConfiguration languageConfiguration;

    private ApplicationConfiguration applicationConfiguration;

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

        doThrow(new ConstraintViolationException(Constraint.ID.UNIQUE_USERNAME)).when(userManager).create(dent);

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
        assertEquals("arthur.dent@hitchhiker.com", node.path("mail").asText());
    }

    @Test
    public void testGetAll() throws URISyntaxException, IOException {
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

        assertTrue(location.getPath().endsWith("users/trillian"));
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

        MockHttpRequest request = MockHttpRequest.put("/users/trillian");
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
    public void testUserImportSuccesfull() throws URISyntaxException, IOException {
        byte[] fileContent = Files.readAllBytes(new File("src/test/java/de/triology/universeadm/user/mockimports/UserImportSuccessfull.csv").toPath());

        MockHttpRequest request = MockHttpRequest.post("/users/import").contentType("text/csv").content(fileContent);
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @Test
    public void testUserImportFailure() throws URISyntaxException, IOException {
        byte[] fileContent = Files.readAllBytes(new File("src/test/java/de/triology/universeadm/user/mockimports/UserImportFailure.csv").toPath());

        MockHttpRequest request = MockHttpRequest.post("/users/import").contentType("text/csv").content(fileContent);
        MockHttpResponse response = Resources.dispatch(resource, request);

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
    }
    //~--- set methods ----------------------------------------------------------

    @Before
    public void setUp() {
        this.userManager = mockUserManager();
        this.groupManager = mockGroupManager();
        this.protocolWriter = mockProtocolWriter();
        this.mailSender = mockMailSender();
        this.languageConfiguration = mockLanguageConfig();
        this.applicationConfiguration = mockApplicationConfig();
        PasswordGenerator pwdGen = new PasswordGenerator();
        this.csvImportManager = new CSVImportManager(userManager, groupManager, protocolWriter, mailSender, pwdGen, languageConfiguration, applicationConfiguration);
        this.resource = new UserResource(userManager, groupManager, csvImportManager);
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

        when(manager.getAll()).thenReturn(all);
        when(manager.getAll(0, 20)).thenReturn(new PagedResultList<>(all, 0, 20,
                1));

        return manager;
    }

    private ProtocolWriter mockProtocolWriter() {
        return mock(ProtocolWriter.class);
    }

    private MailSender mockMailSender() {
        return mock(MailSender.class);
    }

    private LanguageConfiguration mockLanguageConfig() {
        LanguageConfiguration languageConfig = mock(LanguageConfiguration.class);
        when(languageConfig.getStartingProtocol()).thenReturn("---Beginne Protocol---");
        when(languageConfig.getEndingProtocol()).thenReturn("---Beende Protocol---");
        when(languageConfig.getCsvWithLinesReadSuccessful()).thenReturn("CSV-Datei mit %d Zeilen erfolgreich eingelesen.");
        when(languageConfig.getAddedSuccessful()).thenReturn("erfolgreich angelegt(%s, %s, %s, %s, %s)");
        when(languageConfig.getIncompleteLine()).thenReturn("Zeile %d ist unvollständig");
        when(languageConfig.getUserAlreadyExists()).thenReturn("konnte nicht angelegt werden(Nutzer existiert bereits)");
        when(languageConfig.getErrorOnCreatingUser()).thenReturn("Fehler in Zeile %d. Benutzer nicht angelegt. Errors: ");
        when(languageConfig.getEmptyUsername()).thenReturn("Nutzername ist leer");
        when(languageConfig.getEmptyDisplayname()).thenReturn("DisplayName ist leer");
        when(languageConfig.getCouldNotSendMail()).thenReturn("Mail konnte nicht vesendet werden");
        when(languageConfig.getGroupDoesNotExist()).thenReturn("%s existiert nicht");
        when(languageConfig.getUserPartOfGroupAlready()).thenReturn("Nutzer ist bereits Teil von %s");
        when(languageConfig.getUserAdded()).thenReturn("%s zugeordnet");
        when(languageConfig.getEmptyMail()).thenReturn("Mail ist leer");
        when(languageConfig.getEmptySurname()).thenReturn("Surname ist leer");
        when(languageConfig.getUniqueMailError()).thenReturn("Die Mail für diesen Nutzer wird bereits verwendet.");
        return languageConfig;
    }

    private ApplicationConfiguration mockApplicationConfig() {
        ApplicationConfiguration applicationConfig = mock(ApplicationConfiguration.class);
        when(applicationConfig.getSubject()).thenReturn("New Account for CES");
        when(applicationConfig.getContent()).thenReturn("Willkommen zum Cloudogu Ecosystem!\nDies ist ihr Benutzeraccount\nBenutzername = %s\nPasswort = %s\nBei der ersten Anmeldung müssen sie ihr Passwort ändern");
        return applicationConfig;
    }
}
