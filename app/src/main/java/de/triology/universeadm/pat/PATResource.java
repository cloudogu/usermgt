package de.triology.universeadm.pat;

import com.google.inject.Inject;
import de.triology.universeadm.CasConfiguration;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        String casServerUrl = casConfiguration.getServerUrl().replaceAll("/+$", "");
        casPATEndpoint = casServerUrl + "/api/users";
    }

    @GET
    public Response getPATs() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated() || subject.getPrincipal() == null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        String username = subject.getPrincipal().toString();
        HttpURLConnection connection = null;
        try {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8.name())
                .replace("+", "%20");
            URL url = new URL(casPATEndpoint + "/" + encodedUsername + "/pats");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON);
            addBasicAuthentication(connection);

            int status = connection.getResponseCode();
            if (status < 200 || status > 299) {
                throw new IOException("CAS PAT endpoint returned status " + status);
            }

            try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            }
        } catch (IOException e) {
            LOG.error("Failed to load PAT metadata from CAS for current user", e);
            return Response.status(Response.Status.BAD_GATEWAY)
                .entity("{\"message\":\"Failed to load PAT metadata from CAS\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void addBasicAuthentication(HttpURLConnection connection) {
        if (USER != null && PASSWORD != null) {
            String basicAuth = Base64.getEncoder()
                .encodeToString((USER + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
            connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        }
    }
}
