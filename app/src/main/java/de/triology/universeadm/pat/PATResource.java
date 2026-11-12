package de.triology.universeadm.pat;

import com.google.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/pats")
@Produces(MediaType.APPLICATION_JSON)
public class PATResource {
    private final PATClient client;

    @Inject
    public PATResource(PATClient client) {
        this.client = client;
    }

    static String removeTrailingSlashes(String value) {
        return PATClient.removeTrailingSlashes(value);
    }

    /**
     * Retrieves personal access token metadata for the authenticated user.
     *
     * @return the CAS status and JSON body on success, HTTP 403 if the user is
     *         unauthenticated, or HTTP 502 if CAS fails or returns a non-success status
     */
    @GET
    public Response getPATs() {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return client.getPATs(username);
    }

    /**
     * Creates a personal access token for the authenticated user.
     *
     * @param requestBody JSON payload to forward to CAS
     * @return the CAS status and JSON body, HTTP 403 if the user is unauthenticated,
     *         or HTTP 502 if communication with CAS fails
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPAT(String requestBody) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return client.createPAT(username, requestBody);
    }

    /**
     * Deletes a personal access token belonging to the authenticated user.
     *
     * @param id identifier of the token to delete
     * @return the CAS status without a body, HTTP 403 if the user is unauthenticated,
     *         or HTTP 502 if communication with CAS fails
     */
    @DELETE
    @Path("/{id}")
    public Response deletePAT(@PathParam("id") String id) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return client.deletePAT(username, id);
    }

    /**
     * Resolves the username from the current authenticated Shiro subject.
     *
     * @return the principal as a string, or {@code null} if the subject is
     *         unauthenticated or has no principal
     */
    private String getAuthenticatedUsername() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() || subject.getPrincipal() == null) {
            return null;
        }
        return subject.getPrincipal().toString();
    }

}
