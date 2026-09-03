package de.triology.universeadm.dogu;

import de.triology.universeadm.Resources;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DoguResourceTest {
    @Test
    public void shouldReturnDogus() throws Exception {
        DoguProvider provider = mock(DoguProvider.class);
        when(provider.getDogus()).thenReturn(Arrays.asList(
                new Dogu("jenkins", "Jenkins", Arrays.asList("development", "continuous-integration")),
                new Dogu("scm", "SCM-Manager", Arrays.asList("development"))
        ));

        MockHttpResponse response = Resources.dispatch(
                new DoguResource(provider),
                MockHttpRequest.get("/dogus")
        );
        JsonNode json = Resources.parseJson(response);

        assertEquals(200, response.getStatus());
        assertEquals(2, json.size());
        assertEquals("jenkins", json.get(0).path("name").asText());
        assertEquals("Jenkins", json.get(0).path("displayName").asText());
        assertEquals("development", json.get(0).path("tags").get(0).asText());
        assertEquals("continuous-integration", json.get(0).path("tags").get(1).asText());
    }
}
