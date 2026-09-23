package de.triology.universeadm.pat;

import com.google.inject.Inject;
import de.triology.universeadm.CasConfiguration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Path("/pats")
@Produces(MediaType.APPLICATION_JSON)
public class PATResource {

    private static final Logger LOG = LoggerFactory.getLogger(PATResource.class);
    private static final String USER = System.getProperty("cas.mfa.user");
    private static final String PASSWORD = System.getProperty("cas.mfa.password");
    private final String casPATEndpoint;

    @Inject
    public PATResource(CasConfiguration casConfiguration) {
        String casServerUrl = removeTrailingSlashes(casConfiguration.getServerUrl());
        casPATEndpoint = casServerUrl + "/api/users";
    }

    static String removeTrailingSlashes(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') {
            end--;
        }
        return value.substring(0, end);
    }

    @GET
    public Response getPATs() {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return executeRequest(username, "GET", "", null, ResponseMode.SUCCESS_BODY,
            "Failed to load PAT metadata from CAS");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPAT(String requestBody) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return executeRequest(username, "POST", "", requestBody, ResponseMode.BODY,
            "Failed to create PAT in CAS");
    }

    @DELETE
    @Path("/{id}")
    public Response deletePAT(@PathParam("id") String id) {
        String username = getAuthenticatedUsername();
        if (username == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        return executeRequest(username, "DELETE", "/" + id, null, ResponseMode.STATUS_ONLY,
            "Failed to delete PAT in CAS");
    }

    private String getAuthenticatedUsername() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() || subject.getPrincipal() == null) {
            return null;
        }
        return subject.getPrincipal().toString();
    }

    private Response executeRequest(String username, String method, String pathSuffix, String requestBody,
                                    ResponseMode responseMode, String errorMessage) {
        HttpURLConnection connection = null;
        try {
            connection = openConnection(username, pathSuffix, method, requestBody != null);
            writeRequestBody(connection, requestBody);
            return createResponse(connection, responseMode);
        } catch (IOException e) {
            LOG.error(errorMessage + " for current user", e);
            return badGateway(errorMessage);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpURLConnection openConnection(String username, String pathSuffix, String method,
                                             boolean hasRequestBody) throws IOException {
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.name())
            .replace("+", "%20");
        URL url = new URL(casPATEndpoint + "/" + encodedUsername + "/pats" + pathSuffix);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
        connection.setDoOutput(hasRequestBody);
        if (hasRequestBody) {
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);
        }
        addBasicAuthentication(connection);
        return connection;
    }

    private void writeRequestBody(HttpURLConnection connection, String requestBody) throws IOException {
        if (requestBody != null) {
            try (OutputStream output = connection.getOutputStream()) {
                output.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    private Response createResponse(HttpURLConnection connection, ResponseMode responseMode) throws IOException {
        int status = connection.getResponseCode();
        if (responseMode == ResponseMode.SUCCESS_BODY && (status < 200 || status > 299)) {
            throw new IOException("CAS PAT endpoint returned status " + status);
        }
        if (responseMode == ResponseMode.STATUS_ONLY) {
            return Response.status(status).build();
        }
        return Response.status(status)
            .entity(readResponseBody(connection, status))
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private Response badGateway(String message) {
        return Response.status(Response.Status.BAD_GATEWAY)
            .entity("{\"message\":\"" + message + "\"}")
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    private String readResponseBody(HttpURLConnection connection, int status) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            status >= 200 && status < 300 ? connection.getInputStream() : connection.getErrorStream(),
            StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private void addBasicAuthentication(HttpURLConnection connection) {
        if (USER != null && PASSWORD != null) {
            String basicAuth = Base64.getEncoder()
                .encodeToString((USER + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }
    }

    private enum ResponseMode {
        SUCCESS_BODY,
        BODY,
        STATUS_ONLY
    }
}
