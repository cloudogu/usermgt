package de.triology.universeadm;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
  configuration = "classpath:de/triology/universeadm/shiro.001.ini"
)
public class SubjectResourceTest
{

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void testSubject() throws URISyntaxException, IOException
  {
    MockHttpRequest req = MockHttpRequest.get("/subject");
    MockHttpResponse res = Resources.dispatch(new SubjectResource(), req);
    JsonNode node = Resources.parseJson(res);
    assertEquals("trillian", node.path("principal").asText());
    assertTrue(node.path("admin").asBoolean());
  }
  
  @Test
  public void testSubjectUnauthenticated() throws URISyntaxException, IOException{
    MockHttpRequest req = MockHttpRequest.get("/subject");
    MockHttpResponse res = Resources.dispatch(new SubjectResource(), req);
    assertEquals(HttpServletResponse.SC_FORBIDDEN, res.getStatus());
  }
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
  
}
