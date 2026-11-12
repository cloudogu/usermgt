package de.triology.universeadm.robots;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@SubjectAware(configuration = "classpath:de/triology/universeadm/shiro.001.ini")
public class RobotResourceTest {
    @Rule
    public ShiroRule shiro = new ShiroRule();
    private RobotService service;
    private RobotResource resource;

    @Before
    public void setUp() {
        service = mock(RobotService.class);
        resource = new RobotResource(service);
    }

    @Test
    public void rejectsUnauthenticatedRequests() {
        assertEquals(403, resource.getRobots().getStatus());
        assertEquals(403, resource.getRobot("robot-a", "pat-id").getStatus());
        assertEquals(403, resource.deleteRobot("robot-a").getStatus());
        assertEquals(403, resource.renewRobot("robot-a", null).getStatus());
        assertEquals(403, resource.createRobot(new CreateRobotRequest()).getStatus());
        verifyZeroInteractions(service);
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void rejectsNonAdministrators() {
        assertEquals(403, resource.getRobots().getStatus());
        assertEquals(403, resource.getRobot("robot-a", "pat-id").getStatus());
        assertEquals(403, resource.deleteRobot("robot-a").getStatus());
        assertEquals(403, resource.renewRobot("robot-a", null).getStatus());
        assertEquals(403, resource.createRobot(new CreateRobotRequest()).getStatus());
        verifyZeroInteractions(service);
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsRobotsForAdministrators() throws Exception {
        when(service.getRobots()).thenReturn(Collections.emptyList());
        assertEquals("[]", resource.getRobots().getEntity());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsRobotByUserAndPATIdForAdministrators() throws Exception {
        RobotPAT pat = new com.google.gson.Gson().fromJson(
            "{\"id\":\"pat-id\",\"userId\":\"robot-a\",\"scope\":\"/scm\"}", RobotPAT.class);
        when(service.getRobot("robot-a", "pat-id")).thenReturn(pat);
        javax.ws.rs.core.Response response = resource.getRobot("robot-a", "pat-id");
        assertEquals(200, response.getStatus());
        assertEquals("application/json", response.getMediaType().toString());
        RobotPAT returned = new com.google.gson.Gson().fromJson((String) response.getEntity(), RobotPAT.class);
        assertEquals("pat-id", returned.getId());
        assertEquals("robot-a", returned.getUserId());
        assertEquals("/scm", returned.getScope());
        verify(service).getRobot("robot-a", "pat-id");
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsNotFoundForUnknownUserOrPAT() throws Exception {
        assertEquals(404, resource.getRobot("unknown", "pat-id").getStatus());
        assertEquals(404, resource.getRobot("robot-a", "unknown").getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void mapsRobotDetailFailures() throws Exception {
        when(service.getRobot("robot-a", "cas-failure")).thenThrow(new IOException("CAS unavailable"));
        when(service.getRobot("robot-a", "ldap-failure")).thenThrow(new LDAPException(ResultCode.CONNECT_ERROR));
        when(service.getRobot("robot-a", "")).thenThrow(new IllegalArgumentException("PAT ID is required"));
        assertEquals(502, resource.getRobot("robot-a", "cas-failure").getStatus());
        assertEquals(500, resource.getRobot("robot-a", "ldap-failure").getStatus());
        assertEquals(400, resource.getRobot("robot-a", "").getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void mapsCASFailuresToBadGateway() throws Exception {
        when(service.getRobots()).thenThrow(new IOException("CAS unavailable"));
        assertEquals(502, resource.getRobots().getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void mapsLDAPFailuresToServerError() throws Exception {
        when(service.getRobots()).thenThrow(new LDAPException(ResultCode.CONNECT_ERROR));
        assertEquals(500, resource.getRobots().getStatus());
    }
    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsCreatedPATWithToken() throws Exception {
        CreateRobotRequest request = new CreateRobotRequest();
        CreatedRobotPAT pat = new com.google.gson.Gson().fromJson(
            "{\"id\":\"new-pat\",\"displayName\":\"robot\",\"token\":\"new-secret\"}", CreatedRobotPAT.class);
        when(service.createRobot(request)).thenReturn(pat);
        javax.ws.rs.core.Response response = resource.createRobot(request);
        assertEquals(201, response.getStatus());
        assertEquals("new-secret", new com.google.gson.Gson().fromJson(
            (String) response.getEntity(), CreatedRobotPAT.class).getToken());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsConflictForExistingUser() throws Exception {
        CreateRobotRequest request = new CreateRobotRequest();
        when(service.createRobot(request)).thenThrow(new LDAPException(ResultCode.ENTRY_ALREADY_EXISTS));
        assertEquals(409, resource.createRobot(request).getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsBadRequestForInvalidCreateData() throws Exception {
        when(service.createRobot(null)).thenThrow(new IllegalArgumentException("Missing request"));
        assertEquals(400, resource.createRobot(null).getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsBadGatewayWhenPATCreationFails() throws Exception {
        CreateRobotRequest request = new CreateRobotRequest();
        when(service.createRobot(request)).thenThrow(new IOException("CAS failed"));
        assertEquals(502, resource.createRobot(request).getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void deletesRobotForAdministrators() throws Exception {
        when(service.deleteRobot("robot-a")).thenReturn(true);
        assertEquals(204, resource.deleteRobot("robot-a").getStatus());
        verify(service).deleteRobot("robot-a");
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsNotFoundForUnknownRobot() throws Exception {
        assertEquals(404, resource.deleteRobot("unknown").getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void mapsDeleteFailures() throws Exception {
        when(service.deleteRobot("cas-failure")).thenThrow(new IOException("CAS unavailable"));
        when(service.deleteRobot("ldap-failure")).thenThrow(new LDAPException(ResultCode.CONNECT_ERROR));
        when(service.deleteRobot("")).thenThrow(new IllegalArgumentException("Username is required"));
        assertEquals(502, resource.deleteRobot("cas-failure").getStatus());
        assertEquals(500, resource.deleteRobot("ldap-failure").getStatus());
        assertEquals(400, resource.deleteRobot("").getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsRenewedPATWithToken() throws Exception {
        RenewRobotRequest request = new RenewRobotRequest();
        request.setExpiredAt("2099-01-01T00:00:00Z");
        CreatedRobotPAT pat = new com.google.gson.Gson().fromJson(
            "{\"id\":\"replacement\",\"token\":\"new-secret\"}", CreatedRobotPAT.class);
        when(service.renewRobot("robot-a", request)).thenReturn(pat);
        javax.ws.rs.core.Response response = resource.renewRobot("robot-a", request);
        assertEquals(201, response.getStatus());
        CreatedRobotPAT returned = new com.google.gson.Gson().fromJson((String) response.getEntity(), CreatedRobotPAT.class);
        assertEquals("replacement", returned.getId());
        assertEquals("new-secret", returned.getToken());
        verify(service).renewRobot("robot-a", request);
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsNotFoundForUnknownRenewalPAT() throws Exception {
        assertEquals(404, resource.renewRobot("unknown", null).getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void mapsRenewalFailures() throws Exception {
        when(service.renewRobot("cas-failure", null)).thenThrow(new IOException("CAS unavailable"));
        when(service.renewRobot("ldap-failure", null)).thenThrow(new LDAPException(ResultCode.CONNECT_ERROR));
        when(service.renewRobot("", null)).thenThrow(new IllegalArgumentException("Username is required"));
        assertEquals(502, resource.renewRobot("cas-failure", null).getStatus());
        assertEquals(500, resource.renewRobot("ldap-failure", null).getStatus());
        assertEquals(400, resource.renewRobot("", null).getStatus());
    }

}
