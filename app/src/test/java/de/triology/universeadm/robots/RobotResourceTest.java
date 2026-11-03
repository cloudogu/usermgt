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
        assertEquals(403, resource.createRobot(new CreateRobotRequest()).getStatus());
        verifyZeroInteractions(service);
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void rejectsNonAdministrators() {
        assertEquals(403, resource.getRobots().getStatus());
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

}
