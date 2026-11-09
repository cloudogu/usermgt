package de.triology.universeadm.dogu;

import de.triology.universeadm.Resources;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class DoguResourceTest {
    @Test
    public void shouldReturnDogus() throws Exception {
        assertReturnsAllDogus("/dogus");
    }

    @Test
    public void shouldReturnDogusWhenSystemDogusAreIncluded() throws Exception {
        assertReturnsAllDogus("/dogus?systemdogus=true");
    }

    @Test
    public void shouldExcludeDogusWithoutWarpTagWhenSystemDogusAreExcluded() throws Exception {
        MockHttpResponse response = getDogus("/dogus?systemdogus=false");
        JsonNode json = Resources.parseJson(response);

        assertEquals(200, response.getStatus());
        assertEquals(2, json.size());
        assertEquals("jenkins", json.get(0).path("name").asText());
        assertEquals("scm", json.get(1).path("name").asText());
    }

    private MockHttpResponse getDogus(String uri) throws Exception {
        DoguProvider provider = () -> Arrays.asList(
                new Dogu("jenkins", "Jenkins", Arrays.asList("development", "continuous-integration", "warp"), "Development"),
                new Dogu("cas", "CAS", Arrays.asList("authentication"), "Base"),
                new Dogu("scm", "SCM-Manager", Arrays.asList("development", "warp"), "Development"),
                new Dogu("postfix", "Postfix", Collections.emptyList(), "Base"),
                new Dogu("ldap", "LDAP", null, "Base")
        );

        return Resources.dispatch(
                new DoguResource(provider),
                MockHttpRequest.get(uri)
        );
    }

    private void assertReturnsAllDogus(String uri) throws Exception {
        MockHttpResponse response = getDogus(uri);
        JsonNode json = Resources.parseJson(response);

        assertEquals(200, response.getStatus());
        assertEquals(5, json.size());
        assertEquals("jenkins", json.get(0).path("name").asText());
        assertEquals("Development", json.get(0).path("category").asText());
        assertEquals("Jenkins", json.get(0).path("displayName").asText());
        assertEquals("development", json.get(0).path("tags").get(0).asText());
        assertEquals("continuous-integration", json.get(0).path("tags").get(1).asText());
        assertEquals("cas", json.get(1).path("name").asText());
        assertEquals("scm", json.get(2).path("name").asText());
        assertEquals("postfix", json.get(3).path("name").asText());
        assertEquals("ldap", json.get(4).path("name").asText());
    }
}
