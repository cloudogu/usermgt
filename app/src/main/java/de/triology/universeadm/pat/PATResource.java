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

    private enum ResponseMode {
        SUCCESS_BODY,
        BODY,
        STATUS_ONLY
    }

    /**
     * Creates a resource that forwards personal access token requests to CAS.
     *
     * @param casConfiguration configuration containing the CAS server URL
     */
    @Inject
    public PATResource(CasConfiguration casConfiguration) {
        String casServerUrl = removeTrailingSlashes(casConfiguration.getServerUrl());
        casPATEndpoint = casServerUrl + "/api/users";
    }

    /**
     * Removes all trailing slashes from the given value.
     *
     * @param value value to normalize
     * @return the value without trailing slashes
     */
    static String removeTrailingSlashes(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == '/') {
            end--;
        }
        return value.substring(0, end);
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
        return executeRequest(username, "GET", "", null, ResponseMode.SUCCESS_BODY,
            "Failed to load PAT metadata from CAS");
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
        return executeRequest(username, "POST", "", requestBody, ResponseMode.BODY,
            "Failed to create PAT in CAS");
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
        return executeRequest(username, "DELETE", "/" + id, null, ResponseMode.STATUS_ONLY,
            "Failed to delete PAT in CAS");
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

    /**
     * Executes a CAS request and disconnects the connection after processing it.
     *
     * @param username user whose personal access tokens are addressed
     * @param method HTTP request method
     * @param pathSuffix path appended to the user's token endpoint
     * @param requestBody request payload, or {@code null} for no body
     * @param responseMode determines whether to forward the body or require success
     * @param errorMessage message to log and return when an I/O error occurs
     * @return the converted CAS response, or HTTP 502 on an I/O error
     */
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

    /**
     * Configures a connection to the user's CAS personal access token endpoint.
     *
     * @param username username to encode as a URL path segment
     * @param pathSuffix path appended to the user's token endpoint
     * @param method HTTP request method
     * @param hasRequestBody whether to enable output and set the JSON content type
     * @return the configured connection with basic authentication when available
     * @throws IOException if the URL or connection cannot be created or configured
     */
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

    /**
     * Writes a non-null request body to the connection using UTF-8.
     *
     * @param connection connection receiving the payload
     * @param requestBody payload to write, or {@code null} to skip writing
     * @throws IOException if the output stream cannot be opened, written, or closed
     */
    private void writeRequestBody(HttpURLConnection connection, String requestBody) throws IOException {
        if (requestBody != null) {
            try (OutputStream output = connection.getOutputStream()) {
                output.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Converts the CAS response according to the requested response mode.
     *
     * @param connection connection providing the CAS response
     * @param responseMode determines whether to include a body or require a 2xx status
     * @return a response with the CAS status and, when requested, its JSON body
     * @throws IOException if the response cannot be read or a non-2xx status is
     *         received in {@code SUCCESS_BODY} mode
     */
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

    /**
     * Builds an HTTP 502 response containing a JSON error message.
     *
     * @param message error message to include in the response
     * @return the bad gateway response
     */
    private Response badGateway(String message) {
        return Response.status(Response.Status.BAD_GATEWAY)
            .entity("{\"message\":\"" + message + "\"}")
            .type(MediaType.APPLICATION_JSON)
            .build();
    }

    /**
     * Reads the CAS response as UTF-8, concatenating lines without line separators.
     * Uses the input stream for 2xx responses and the error stream otherwise.
     *
     * @param connection connection providing the response body
     * @param status HTTP response status used to select the stream
     * @return the response body without line separators
     * @throws IOException if the response stream cannot be read or closed
     */
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

    /**
     * Adds basic authentication when both CAS service credentials are configured.
     *
     * @param connection connection to receive the Authorization header
     */
    private void addBasicAuthentication(HttpURLConnection connection) {
        if (USER != null && PASSWORD != null) {
            String basicAuth = Base64.getEncoder()
                .encodeToString((USER + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }
    }

}
