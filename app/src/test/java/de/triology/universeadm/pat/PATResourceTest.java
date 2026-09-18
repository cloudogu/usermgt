package de.triology.universeadm.pat;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import de.triology.universeadm.CasConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@SubjectAware(configuration = "classpath:de/triology/universeadm/shiro.001.ini")
public class PATResourceTest {
    private final AtomicReference<String> method = new AtomicReference<>();
    private final AtomicReference<String> path = new AtomicReference<>();
    private final AtomicReference<String> body = new AtomicReference<>();
    private HttpServer server;
    private PATResource resource;
    private int responseStatus;
    private String responseBody;

    @Rule
    public ShiroRule shiro = new ShiroRule();

    @Before
    public void setUp() throws Exception {
        responseStatus = 200;
        responseBody = "[]";
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/api/users", this::handleRequest);
        server.start();

        CasConfiguration configuration = new CasConfiguration();
        Field serverUrl = CasConfiguration.class.getDeclaredField("serverUrl");
        serverUrl.setAccessible(true);
        serverUrl.set(configuration, "http://localhost:" + server.getAddress().getPort() + "/");
        resource = new PATResource(configuration);
    }

    @After
    public void tearDown() {
        server.stop(0);
    }

    @Test
    public void shouldRejectUnauthenticatedRequests() {
        assertEquals(403, resource.getPATs().getStatus());
        assertEquals(403, resource.createPAT("{}").getStatus());
        assertEquals(403, resource.deletePAT("token-id").getStatus());
        assertNull(method.get());
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void shouldLoadPATsForCurrentUser() {
        responseBody = "[{\"id\":\"first\"}]";

        Response response = resource.getPATs();

        assertEquals(200, response.getStatus());
        assertEquals("[{\"id\":\"first\"}]", response.getEntity());
        assertEquals("GET", method.get());
        assertEquals("/api/users/dent/pats", path.get());
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void shouldMapFailedGetToBadGateway() {
        responseStatus = 500;
        responseBody = "failure";

        Response response = resource.getPATs();

        assertEquals(502, response.getStatus());
        assertEquals("{\"message\":\"Failed to load PAT metadata from CAS\"}", response.getEntity());
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void shouldForwardCreateBodyStatusAndErrorBody() {
        responseStatus = 422;
        responseBody = "{\"message\":\"invalid expiration\"}";

        Response response = resource.createPAT("{\"name\":\"build token\"}");

        assertEquals(422, response.getStatus());
        assertEquals(responseBody, response.getEntity());
        assertEquals("POST", method.get());
        assertEquals("{\"name\":\"build token\"}", body.get());
        assertEquals("/api/users/dent/pats", path.get());
    }

    @Test
    @SubjectAware(username = "dent", password = "secret")
    public void shouldForwardDeleteStatusAndTokenId() {
        responseStatus = 204;
        responseBody = "";

        Response response = resource.deletePAT("token-42");

        assertEquals(204, response.getStatus());
        assertEquals("DELETE", method.get());
        assertEquals("/api/users/dent/pats/token-42", path.get());
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        method.set(exchange.getRequestMethod());
        path.set(exchange.getRequestURI().getRawPath());
        body.set(read(exchange.getRequestBody()));
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(responseStatus, responseStatus == 204 ? -1 : bytes.length);
        if (responseStatus != 204) {
            exchange.getResponseBody().write(bytes);
        }
        exchange.close();
    }

    private String read(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int count;
        while ((count = input.read(buffer)) >= 0) {
            output.write(buffer, 0, count);
        }
        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }
}
