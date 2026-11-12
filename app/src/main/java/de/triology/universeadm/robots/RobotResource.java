package de.triology.universeadm.robots;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import de.triology.universeadm.Roles;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/robots")
@Produces(MediaType.APPLICATION_JSON)
public class RobotResource {
    private static final Logger LOG = LoggerFactory.getLogger(RobotResource.class);
    private static final Gson GSON = new Gson();
    private final RobotService service;

    @Inject
    public RobotResource(RobotService service) {
        this.service = service;
    }

    @GET
    public Response getRobots() {
        if (!isAuthenticatedAdministrator()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        try {
            return Response.ok(GSON.toJson(service.getRobots()), MediaType.APPLICATION_JSON).build();
        } catch (LDAPException e) {
            LOG.error("Failed to load technical users from LDAP", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(GSON.toJson(java.util.Collections.singletonMap("message", "Failed to load technical users from LDAP"))).build();
        } catch (IOException e) {
            LOG.error("Failed to load robot PAT metadata from CAS", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to load robot PAT metadata from CAS\"}").build();
        }
    }
    @GET
    @Path("/{userId}/{id}")
    public Response getRobot(@PathParam("userId") String userId, @PathParam("id") String id) {
        if (!isAuthenticatedAdministrator()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        try {
            RobotPAT pat = service.getRobot(userId, id);
            if (pat == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(GSON.toJson(pat), MediaType.APPLICATION_JSON).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(GSON.toJson(java.util.Collections.singletonMap("message", e.getMessage()))).build();
        } catch (LDAPException e) {
            LOG.error("Failed to load technical user from LDAP", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Failed to load technical user from LDAP\"}").build();
        } catch (IOException e) {
            LOG.error("Failed to load robot PAT metadata from CAS", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to load robot PAT metadata from CAS\"}").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRobot(CreateRobotRequest request) {
        if (!isAuthenticatedAdministrator()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        try {
            CreatedRobotPAT pat = service.createRobot(request);
            return Response.status(Response.Status.CREATED)
                .entity(GSON.toJson(pat)).type(MediaType.APPLICATION_JSON).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(GSON.toJson(java.util.Collections.singletonMap("message", e.getMessage())))
                .type(MediaType.APPLICATION_JSON).build();
        } catch (LDAPException e) {
            if (e.getResultCode() == ResultCode.ENTRY_ALREADY_EXISTS) {
                return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"A user with this name already exists\"}")
                    .type(MediaType.APPLICATION_JSON).build();
            }
            LOG.error("Failed to create technical user in LDAP", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Failed to create technical user in LDAP\"}")
                .type(MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            LOG.error("Failed to create robot PAT in CAS", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to create robot PAT in CAS\"}")
                .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @PUT
    @Path("/{username}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response renewRobot(@PathParam("username") String username, RenewRobotRequest request) {
        if (!isAuthenticatedAdministrator()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        try {
            CreatedRobotPAT pat = service.renewRobot(username, request);
            if (pat == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.CREATED)
                .entity(GSON.toJson(pat)).type(MediaType.APPLICATION_JSON).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(GSON.toJson(java.util.Collections.singletonMap("message", e.getMessage()))).build();
        } catch (LDAPException e) {
            LOG.error("Failed to load technical user from LDAP for PAT renewal", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Failed to load technical user from LDAP\"}").build();
        } catch (IOException e) {
            LOG.error("Failed to renew robot PAT in CAS", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to renew robot PAT in CAS\"}").build();
        }
    }

    @DELETE
    @Path("/{username}")
    public Response deleteRobot(@PathParam("username") String username) {
        if (!isAuthenticatedAdministrator()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        try {
            return service.deleteRobot(username)
                ? Response.noContent().build()
                : Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(GSON.toJson(java.util.Collections.singletonMap("message", e.getMessage()))).build();
        } catch (LDAPException e) {
            LOG.error("Failed to delete technical user from LDAP", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"message\":\"Failed to delete technical user from LDAP\"}").build();
        } catch (IOException e) {
            LOG.error("Failed to delete robot PAT in CAS", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to delete robot PAT in CAS\"}").build();
        }
    }

    private boolean isAuthenticatedAdministrator() {
        Subject subject = SecurityUtils.getSubject();
        return subject.isAuthenticated() && subject.hasRole(Roles.ADMINISTRATOR);
    }
}
