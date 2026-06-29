package de.triology.universeadm.multifactor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Path("/mfa")
@Produces(MediaType.APPLICATION_JSON)
public class MultifactorResource {

    private static final Logger LOG = LoggerFactory.getLogger(MultifactorResource.class);
    private static final Gson GSON = new Gson();
    private static final String FQDN = System.getProperty("cas.mfa.fqdn");
    private static final String user = System.getProperty("cas.mfa.user");
    private static final String password = System.getProperty("cas.mfa.password");
    private static final String CAS_MFA_ENDPOINT = "https://" + FQDN + "/cas/actuator/gauthCredentialRepository";

    public MultifactorResource() {
    }

    @GET
    @Path("/{username}")
    public Response getMfa(@PathParam("username") String username) {
        try {
            String jsonResponse = callCasMfaGetApi(username);
            MfaDTO credentials = parse(jsonResponse);
            return Response.ok(credentials, MediaType.APPLICATION_JSON).build();
        } catch (IOException e) {
            LOG.error("Failed to load MFA data from CAS", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to load MFA data from CAS\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    @DELETE
    @Path("/{username}")
    public Response deleteMfa(@PathParam("username") String username) {
        try {
            callCasMfaDeleteApi(username);
            return Response.noContent().build();
        } catch (IOException e) {
            LOG.error("Failed to delete MFA credentials for user: {}", username, e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to delete MFA credentials\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    /**
     * Parses the JSON response from the CAS API and extracts only username and (device-)name.
     */
    private MfaDTO parse(String jsonResponse) {
        MfaDTO result = new MfaDTO();

        try {
            JsonArray jsonArray = GSON.fromJson(jsonResponse, JsonArray.class);

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();

                String username = obj.has("username") ? obj.get("username").getAsString() : null;
                String name = obj.has("name") ? obj.get("name").getAsString() : null;

                result.setUsername(username);
                result.setName(name);
            }
        } catch (JsonParseException | IllegalStateException e) {
            LOG.error("Failed to parse MFA credentials JSON", e);
        }

        return result;
    }

    /**
     * Calls the CAS MFA API to list all MFA credentials.
     */
    private String callCasMfaGetApi(String username) throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(CAS_MFA_ENDPOINT + "/" + username);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            addBasicAuthentication(connection);

            int status = connection.getResponseCode();
            if (status < 200 || status > 299) {
                throw new IOException("CAS MFA endpoint returned status " + status);
            }

            try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Calls the CAS MFA API to delete MFA credentials for a user.
     *
     * @param username username of the user whose MFA credentials should be deleted
     */
    private void callCasMfaDeleteApi(String username) throws IOException {
        HttpURLConnection connection = null;
        try {
            String urlString = CAS_MFA_ENDPOINT + "/" + username;
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");

            addBasicAuthentication(connection);

            int status = connection.getResponseCode();

            if ((status < 200 || status > 299)) {
                throw new IOException("CAS MFA endpoint returned status " + status);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Adds Basic Authentication Header to the HTTP connection.
     *
     * @param connection die HTTP-Verbindung
     */
    private void addBasicAuthentication(HttpURLConnection connection) {
        if (user != null && password != null) {
            String basicAuth = Base64.getEncoder()
                .encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }
    }

    private HttpURLConnection createConnection(String username, String method) throws IOException {
        HttpURLConnection connection = null;
        String urlString = CAS_MFA_ENDPOINT + "/" + username;
        URL url = new URL(urlString);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        addBasicAuthentication(connection);

        return connection;
    }
}
