package de.triology.universeadm.robots;

import com.google.gson.Gson;
import com.github.legman.EventBus;
import de.triology.universeadm.LDAPHasher;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.MappingAttribute;
import de.triology.universeadm.pat.PATClient;
import de.triology.universeadm.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RobotServiceTest {
    private RobotService service;
    private PATClient client;
    private LDAPInterface connection;
    private LDAPConfiguration configuration;
    private Mapper<User> mapper;
    private LDAPHasher hasher;
    private EventBus eventBus;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        LDAPConnectionStrategy strategy = mock(LDAPConnectionStrategy.class);
        configuration = mock(LDAPConfiguration.class);
        MapperFactory factory = mock(MapperFactory.class);
        mapper = mock(Mapper.class);
        hasher = mock(LDAPHasher.class);
        eventBus = mock(EventBus.class);
        connection = mock(LDAPInterface.class);
        client = mock(PATClient.class);
        when(strategy.get()).thenReturn(connection);
        when(configuration.getUserBaseDN()).thenReturn("ou=People,dc=example");
        when(factory.createMapper(User.class, "ou=People,dc=example")).thenReturn(mapper);
        when(mapper.getParentDN()).thenReturn("ou=People,dc=example");
        when(mapper.getBaseFilter()).thenReturn(Filter.createPresenceFilter("uid"));
        when(mapper.getAttribute("username")).thenReturn(
            new MappingAttribute.MappingAttributeBuilder("username").ldapName("uid").build());
        when(mapper.getAttribute("displayName")).thenReturn(
            new MappingAttribute.MappingAttributeBuilder("displayName").ldapName("cn").build());
        when(mapper.getAttribute("mail")).thenReturn(
            new MappingAttribute.MappingAttributeBuilder("mail").build());
        service = new RobotService(strategy, configuration, factory, client, hasher, eventBus);
    }

    @Test
    public void loadsAllLDAPPagesWithTechnicalFilterAndOnlyRequiredAttributes() throws Exception {
        SearchResult first = page(new ASN1OctetString("next"), user("robot-a", "Robot A"));
        SearchResult last = page(new ASN1OctetString(), user("robot-b", "Robot B"));
        when(connection.search(any(SearchRequest.class))).thenReturn(first, last);

        List<User> users = service.getTechnicalUsers();

        assertEquals(2, users.size());
        ArgumentCaptor<SearchRequest> requests = ArgumentCaptor.forClass(SearchRequest.class);
        verify(connection, times(2)).search(requests.capture());
        SearchRequest request = requests.getValue();
        assertEquals("ou=People,dc=example", request.getBaseDN());
        assertEquals("(&(uid=*)(technical=TRUE))", request.getFilter().toString());
        assertEquals(Arrays.asList("uid", "cn"), request.getAttributeList());
        SimplePagedResultsControl control = (SimplePagedResultsControl) request.getControl(SimplePagedResultsControl.PAGED_RESULTS_OID);
        assertEquals("next", control.getCookie().stringValue());
    }

    @Test
    public void returnsFirstPATPerUserWithLDAPDisplayNameAndOmitsSecrets() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString(),
            user("robot-a", "Robot A"), user("robot-b", "Robot B")));
        when(client.getPATs("robot-a")).thenReturn(Response.ok(
            "[{\"id\":\"first\",\"userId\":\"robot-a\",\"displayName\":\"original\",\"token\":\"secret\"},{\"id\":\"second\"}]").build());
        when(client.getPATs("robot-b")).thenReturn(Response.ok("[{\"id\":\"third\"}]").build());

        List<RobotPAT> robots = service.getRobots();

        assertEquals(2, robots.size());
        assertEquals("first", robots.get(0).getId());
        assertEquals("robot-a", robots.get(0).getUserId());
        assertEquals("Robot A", robots.get(0).getDisplayName());
        assertEquals("third", robots.get(1).getId());
        assertFalse(new Gson().toJson(robots).contains("secret"));
    }

    @Test
    public void skipsUsersWithoutPATAndFallsBackToUsername() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString(),
            user("robot-a", " "), user("robot-b", "Robot B"), user("robot-c", "Robot C")));
        when(client.getPATs("robot-a")).thenReturn(Response.ok("[{\"id\":\"first\"}]").build());
        when(client.getPATs("robot-b")).thenReturn(Response.ok("[]").build());
        when(client.getPATs("robot-c")).thenReturn(Response.noContent().build());

        List<RobotPAT> robots = service.getRobots();

        assertEquals(1, robots.size());
        assertEquals("robot-a", robots.get(0).getDisplayName());
    }

    @Test
    public void doesNotCallCASWhenNoTechnicalUsersExist() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString()));
        assertTrue(service.getRobots().isEmpty());
        verifyZeroInteractions(client);
    }

    @Test(expected = IOException.class)
    public void failsRatherThanReturningPartialResultsOnCASError() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString(), user("robot-a", "Robot A")));
        when(client.getPATs("robot-a")).thenReturn(Response.status(502).build());
        service.getRobots();
    }

    @Test(expected = IOException.class)
    public void rejectsMalformedCASMetadata() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString(), user("robot-a", "Robot A")));
        when(client.getPATs("robot-a")).thenReturn(Response.ok("{}").build());
        service.getRobots();
    }

    @Test
    public void createsTechnicalUserAndPATWithRequestedSettings() throws Exception {
        prepareCreate();
        CreateRobotRequest request = createRequest();
        request.setExpiresAt("2099-01-01T00:00:00Z");
        request.setScope("/scm");
        when(client.createPAT(eq("build-robot"), anyString())).thenReturn(Response.status(201)
            .entity("{\"id\":\"created\",\"token\":\"pat-secret\",\"displayName\":\"CAS-name\"}").build());

        CreatedRobotPAT pat = service.createRobot(request);

        ArgumentCaptor<User> users = ArgumentCaptor.forClass(User.class);
        verify(mapper).convert(users.capture());
        User user = users.getValue();
        assertEquals("build-robot", user.getUsername());
        assertEquals("build-robot", user.getDisplayName());
        assertEquals("build-robot", user.getGivenname());
        assertEquals("build-robot", user.getSurname());
        assertEquals("build-robot-robot@account.de", user.getMail());
        assertNull(user.getPassword());
        assertFalse(user.isPwdReset());
        ArgumentCaptor<com.unboundid.ldap.sdk.Entry> entries = ArgumentCaptor.forClass(com.unboundid.ldap.sdk.Entry.class);
        verify(connection).add(entries.capture());
        assertEquals("TRUE", entries.getValue().getAttributeValue("technical"));
        assertEquals("hashed-password", entries.getValue().getAttributeValue("userPassword"));
        ArgumentCaptor<String> passwords = ArgumentCaptor.forClass(String.class);
        verify(hasher).hash(passwords.capture());
        assertTrue(passwords.getValue().length() >= 43);
        assertEquals("build-robot", pat.getDisplayName());
        assertEquals("pat-secret", pat.getToken());
        assertFalse(new Gson().toJson(pat).contains(passwords.getValue()));
        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        verify(client).createPAT(eq("build-robot"), bodies.capture());
        CreateRobotRequest forwarded = new Gson().fromJson(bodies.getValue(), CreateRobotRequest.class);
        assertEquals(request.getExpiresAt(), forwarded.getExpiresAt());
        assertEquals(request.getScope(), forwarded.getScope());
        verify(eventBus).post(any(de.triology.universeadm.user.UserEvent.class));
    }

    @Test
    public void rollsBackUserWhenPATCreationFails() throws Exception {
        prepareCreate();
        when(client.createPAT(eq("build-robot"), anyString())).thenReturn(Response.status(500).build());
        try {
            service.createRobot(createRequest());
            fail("Expected CAS failure");
        } catch (IOException expected) {
            verify(connection).delete("uid=build-robot,ou=People,dc=example");
            verifyZeroInteractions(eventBus);
        }
    }

    @Test
    public void rollsBackUserWhenPATResponseHasNoToken() throws Exception {
        prepareCreate();
        when(client.createPAT(eq("build-robot"), anyString())).thenReturn(Response.ok("{\"id\":\"created\"}").build());
        try {
            service.createRobot(createRequest());
            fail("Expected incomplete CAS response failure");
        } catch (IOException expected) {
            verify(connection).delete("uid=build-robot,ou=People,dc=example");
        }
    }

    @Test
    public void neverDeletesExistingUserOnNameConflict() throws Exception {
        prepareCreate();
        when(connection.add(any(com.unboundid.ldap.sdk.Entry.class))).thenThrow(
            new com.unboundid.ldap.sdk.LDAPException(com.unboundid.ldap.sdk.ResultCode.ENTRY_ALREADY_EXISTS));
        try {
            service.createRobot(createRequest());
            fail("Expected duplicate user failure");
        } catch (com.unboundid.ldap.sdk.LDAPException expected) {
            verify(connection, never()).delete(anyString());
            verifyZeroInteractions(client);
        }
    }

    @Test
    public void rejectsInvalidNamesBeforeWritingLDAP() throws Exception {
        CreateRobotRequest request = createRequest();
        request.setDisplayName("invalid name");
        try {
            service.createRobot(request);
            fail("Expected invalid name");
        } catch (IllegalArgumentException expected) {
            verifyZeroInteractions(connection, client);
        }
    }

    @Test
    public void normalizesUsernameForRobotMail() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(mock(SearchResult.class));
        assertEquals("build-bot-example-robot@account.de", service.createRobotMail("Build_Bot@Example", connection));
        assertEquals("account-robot@account.de", service.createRobotMail("__", connection));
        assertEquals("long-name-robot@account.de", service.createRobotMail("..Long.Name..", connection));
    }

    @Test
    public void avoidsExistingMailAfterNormalization() throws Exception {
        SearchResult occupied = mock(SearchResult.class);
        when(occupied.getEntryCount()).thenReturn(1);
        when(connection.search(any(SearchRequest.class))).thenReturn(occupied, mock(SearchResult.class));
        assertEquals("build-bot-2-robot@account.de", service.createRobotMail("Build_Bot", connection));
    }

    @Test
    public void keepsRobotMailLocalPartWithin64Characters() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(mock(SearchResult.class));
        String username = String.join("", java.util.Collections.nCopies(64, "a"));
        String mail = service.createRobotMail(username, connection);
        assertEquals(64, mail.indexOf('@'));
        assertTrue(mail.endsWith("-robot@account.de"));
    }

    @Test
    public void deletesMatchingPATEvenWhenItIsNotFirstAndThenDeletesOwner() throws Exception {
        prepareDelete();
        when(client.deletePAT("robot-a", "target")).thenReturn(Response.noContent().build());

        assertTrue(service.deleteRobot("target"));

        org.mockito.InOrder order = inOrder(client, connection, eventBus);
        order.verify(client).deletePAT("robot-a", "target");
        order.verify(connection).delete("uid=robot-a,ou=People,dc=example");
        ArgumentCaptor<de.triology.universeadm.user.UserEvent> events =
            ArgumentCaptor.forClass(de.triology.universeadm.user.UserEvent.class);
        order.verify(eventBus).post(events.capture());
        assertEquals(de.triology.universeadm.EventType.REMOVE, events.getValue().getType());
        assertEquals("robot-a", events.getValue().getEntity().getUsername());
        ArgumentCaptor<SearchRequest> requests = ArgumentCaptor.forClass(SearchRequest.class);
        verify(connection).searchForEntry(requests.capture());
        assertEquals("(&(uid=*)(technical=TRUE)(uid=robot-a))", requests.getValue().getFilter().toString());
    }

    @Test
    public void doesNotDeleteAnythingForUnknownPAT() throws Exception {
        prepareDelete();
        assertFalse(service.deleteRobot("unknown"));
        verify(client, never()).deletePAT(anyString(), anyString());
        verify(connection, never()).delete(anyString());
        verifyZeroInteractions(eventBus);
    }

    @Test
    public void preservesUserWhenPATDeletionFails() throws Exception {
        prepareDelete();
        when(client.deletePAT("robot-a", "target")).thenReturn(Response.status(502).build());
        try {
            service.deleteRobot("target");
            fail("Expected CAS failure");
        } catch (IOException expected) {
            verify(connection, never()).delete(anyString());
            verifyZeroInteractions(eventBus);
        }
    }

    @Test
    public void doesNotDeleteAnythingWhenPATLookupFails() throws Exception {
        prepareDelete();
        when(client.getPATs("robot-a")).thenReturn(Response.status(502).build());
        try {
            service.deleteRobot("target");
            fail("Expected CAS failure");
        } catch (IOException expected) {
            verify(client, never()).deletePAT(anyString(), anyString());
            verify(connection, never()).delete(anyString());
        }
    }

    @Test
    public void reportsLDAPDeletionFailureWithoutPostingEvent() throws Exception {
        prepareDelete();
        when(client.deletePAT("robot-a", "target")).thenReturn(Response.noContent().build());
        when(connection.delete(anyString())).thenThrow(
            new com.unboundid.ldap.sdk.LDAPException(com.unboundid.ldap.sdk.ResultCode.CONNECT_ERROR));
        try {
            service.deleteRobot("target");
            fail("Expected LDAP failure");
        } catch (com.unboundid.ldap.sdk.LDAPException expected) {
            verifyZeroInteractions(eventBus);
        }
    }

    @Test
    public void rejectsEmptyPATIdBeforeAccessingLDAPOrCAS() throws Exception {
        for (String id : Arrays.asList(null, "", " ")) {
            try {
                service.deleteRobot(id);
                fail("Expected invalid PAT id");
            } catch (IllegalArgumentException expected) {
                verifyZeroInteractions(connection, client);
            }
        }
    }

    @Test
    public void renewsPATWithOriginalExpirationAndScopeAndPreservesUser() throws Exception {
        prepareRenew();

        CreatedRobotPAT renewed = service.renewRobot("target", null);

        assertEquals("replacement", renewed.getId());
        assertEquals("new-secret", renewed.getToken());
        assertEquals("robot-a", renewed.getDisplayName());
        org.mockito.InOrder order = inOrder(client);
        order.verify(client).getPATs("robot-a");
        order.verify(client).deletePAT("robot-a", "target");
        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        order.verify(client).createPAT(eq("robot-a"), bodies.capture());
        CreateRobotRequest replacement = new Gson().fromJson(bodies.getValue(), CreateRobotRequest.class);
        assertEquals("2099-01-01T00:00:00Z", replacement.getExpiresAt());
        assertEquals("/scm", replacement.getScope());
        assertEquals("Original PAT", replacement.getDisplayName());
        verify(connection, never()).add(any(com.unboundid.ldap.sdk.Entry.class));
        verify(connection, never()).delete(anyString());
        verifyZeroInteractions(eventBus);
    }

    @Test
    public void usesOptionalRenewalExpirationOverride() throws Exception {
        prepareRenew();
        RenewRobotRequest request = new Gson().fromJson(
            "{\"expiredAt\":\"2099-02-01T00:00:00Z\"}", RenewRobotRequest.class);

        service.renewRobot("target", request);

        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        verify(client).createPAT(eq("robot-a"), bodies.capture());
        CreateRobotRequest replacement = new Gson().fromJson(bodies.getValue(), CreateRobotRequest.class);
        assertEquals("2099-02-01T00:00:00Z", replacement.getExpiresAt());
        assertEquals("/scm", replacement.getScope());
    }

    @Test
    public void removesExpirationWhenExplicitlyRequested() throws Exception {
        prepareRenew();
        RenewRobotRequest request = new RenewRobotRequest();
        request.setExpiredAt(null);

        service.renewRobot("target", request);

        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        verify(client).createPAT(eq("robot-a"), bodies.capture());
        CreateRobotRequest replacement = new Gson().fromJson(bodies.getValue(), CreateRobotRequest.class);
        assertNull(replacement.getExpiresAt());
        assertEquals("/scm", replacement.getScope());
    }

    @Test
    public void renewsNonExpiringPATWithoutAddingExpiration() throws Exception {
        prepareRenew();
        when(client.getPATs("robot-a")).thenReturn(Response.ok("[{\"id\":\"target\"}]").build());

        service.renewRobot("target", new RenewRobotRequest());

        ArgumentCaptor<String> bodies = ArgumentCaptor.forClass(String.class);
        verify(client).createPAT(eq("robot-a"), bodies.capture());
        CreateRobotRequest replacement = new Gson().fromJson(bodies.getValue(), CreateRobotRequest.class);
        assertNull(replacement.getExpiresAt());
        assertNull(replacement.getScope());
    }

    @Test
    public void doesNotRenewUnknownPAT() throws Exception {
        prepareRenew();
        assertNull(service.renewRobot("unknown", null));
        verify(client, never()).deletePAT(anyString(), anyString());
        verify(client, never()).createPAT(anyString(), anyString());
        verify(connection, never()).delete(anyString());
    }

    @Test
    public void rejectsInvalidRenewalExpirationBeforeDeletingPAT() throws Exception {
        for (String expiration : Arrays.asList("invalid", "", "2000-01-01T00:00:00Z")) {
            RenewRobotRequest request = new RenewRobotRequest();
            request.setExpiredAt(expiration);
            try {
                service.renewRobot("target", request);
                fail("Expected invalid expiration");
            } catch (IllegalArgumentException expected) {
                verifyZeroInteractions(connection, client);
            }
        }
    }

    @Test
    public void rejectsEmptyRenewalPATId() throws Exception {
        for (String id : Arrays.asList(null, "", " ")) {
            try {
                service.renewRobot(id, null);
                fail("Expected invalid PAT id");
            } catch (IllegalArgumentException expected) {
                verifyZeroInteractions(connection, client);
            }
        }
    }

    @Test
    public void stopsRenewalWhenPATDeletionFails() throws Exception {
        prepareRenew();
        when(client.deletePAT("robot-a", "target")).thenReturn(Response.status(502).build());
        try {
            service.renewRobot("target", null);
            fail("Expected CAS failure");
        } catch (IOException expected) {
            verify(client, never()).createPAT(anyString(), anyString());
            verify(connection, never()).delete(anyString());
            verifyZeroInteractions(eventBus);
        }
    }

    @Test
    public void preservesUserWhenReplacementCreationFails() throws Exception {
        prepareRenew();
        when(client.createPAT(eq("robot-a"), anyString())).thenReturn(Response.status(502).build());
        try {
            service.renewRobot("target", null);
            fail("Expected CAS failure");
        } catch (IOException expected) {
            verify(client).deletePAT("robot-a", "target");
            verify(connection, never()).delete(anyString());
            verifyZeroInteractions(eventBus);
        }
    }

    private void prepareRenew() throws Exception {
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString(), user("robot-a", "Robot A")));
        when(client.getPATs("robot-a")).thenReturn(Response.ok(
            "[{\"id\":\"first\"},{\"id\":\"target\",\"userId\":\"untrusted-owner\",\"displayName\":\"Original PAT\","
                + "\"expiresAt\":\"2099-01-01T00:00:00Z\",\"scope\":\"/scm\"}]").build());
        when(client.deletePAT("robot-a", "target")).thenReturn(Response.noContent().build());
        when(client.createPAT(eq("robot-a"), anyString())).thenReturn(Response.status(201)
            .entity("{\"id\":\"replacement\",\"token\":\"new-secret\"}").build());
    }

    private void prepareDelete() throws Exception {
        SearchResultEntry entry = user("robot-a", "Robot A");
        when(connection.search(any(SearchRequest.class))).thenReturn(page(new ASN1OctetString(), entry));
        when(client.getPATs("robot-a")).thenReturn(Response.ok(
            "[{\"id\":\"first\"},{\"id\":\"target\",\"userId\":\"untrusted-owner\"}]").build());
        when(mapper.getReturningAttributes()).thenReturn(Arrays.asList("uid", "cn", "memberOf"));
        when(connection.searchForEntry(any(SearchRequest.class))).thenReturn(entry);
        when(mapper.convert(entry)).thenReturn(new User("robot-a"));
    }

    private void prepareCreate() throws com.unboundid.ldap.sdk.LDAPException {
        when(connection.search(any(SearchRequest.class))).thenReturn(mock(SearchResult.class));
        when(configuration.isRequirePreEncodedPasswords()).thenReturn(true);
        when(hasher.hash(anyString())).thenReturn("hashed-password".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        when(mapper.convert(any(User.class))).thenReturn(new com.unboundid.ldap.sdk.Entry("uid=build-robot,ou=People,dc=example"));
    }

    private CreateRobotRequest createRequest() {
        CreateRobotRequest request = new CreateRobotRequest();
        request.setDisplayName("build-robot");
        return request;
    }

    private SearchResult page(ASN1OctetString cookie, SearchResultEntry... users) {
        SearchResult result = mock(SearchResult.class);
        when(result.getSearchEntries()).thenReturn(Arrays.asList(users));
        when(result.getResponseControl(SimplePagedResultsControl.PAGED_RESULTS_OID))
            .thenReturn(new SimplePagedResultsControl(0, cookie));
        return result;
    }

    private SearchResultEntry user(String username, String displayName) {
        return new SearchResultEntry("uid=" + username + ",ou=People,dc=example", Arrays.asList(
            new com.unboundid.ldap.sdk.Attribute("uid", username),
            new com.unboundid.ldap.sdk.Attribute("cn", displayName)));
    }
}
