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
        assertEquals(403, resource.deleteRobot("pat-id").getStatus());
        assertEquals(403, resource.renewRobot("pat-id", null).getStatus());
        assertEquals(403, resource.createRobot(new CreateRobotRequest()).getStatus());
        verifyZeroInteractions(service);
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void rejectsNonAdministrators() {
        assertEquals(403, resource.getRobots().getStatus());
        assertEquals(403, resource.deleteRobot("pat-id").getStatus());
        assertEquals(403, resource.renewRobot("pat-id", null).getStatus());
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
        when(service.deleteRobot("pat-id")).thenReturn(true);
        assertEquals(204, resource.deleteRobot("pat-id").getStatus());
        verify(service).deleteRobot("pat-id");
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void returnsNotFoundForUnknownRobotPAT() throws Exception {
        assertEquals(404, resource.deleteRobot("unknown").getStatus());
    }

    @Test
    @SubjectAware(username = "trillian", password = "secret")
    public void mapsDeleteFailures() throws Exception {
        when(service.deleteRobot("cas-failure")).thenThrow(new IOException("CAS unavailable"));
        when(service.deleteRobot("ldap-failure")).thenThrow(new LDAPException(ResultCode.CONNECT_ERROR));
        when(service.deleteRobot("")).thenThrow(new IllegalArgumentException("PAT id is required"));
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
        when(service.renewRobot("old-pat", request)).thenReturn(pat);
        javax.ws.rs.core.Response response = resource.renewRobot("old-pat", request);
        assertEquals(201, response.getStatus());
        CreatedRobotPAT returned = new com.google.gson.Gson().fromJson((String) response.getEntity(), CreatedRobotPAT.class);
        assertEquals("replacement", returned.getId());
        assertEquals("new-secret", returned.getToken());
        verify(service).renewRobot("old-pat", request);
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
        when(service.renewRobot("", null)).thenThrow(new IllegalArgumentException("PAT id is required"));
        assertEquals(502, resource.renewRobot("cas-failure", null).getStatus());
        assertEquals(500, resource.renewRobot("ldap-failure", null).getStatus());
        assertEquals(400, resource.renewRobot("", null).getStatus());
    }

}
