package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;

import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
  configuration = "classpath:de/triology/universeadm/shiro.001.ini",
  username = "trillian",
  password = "secret"
)
@RunWith(MockitoJUnitRunner.class)
public class LogoutResourceTest
{

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testLogout() throws IOException, URISyntaxException
  {
    when(cas.getLogoutUrl()).thenReturn("https://www.scm-manager.org");

    MockHttpRequest req = MockHttpRequest.get("/logout");
    MockHttpResponse res = Resources.dispatch(new LogoutResource(cas), req);

    assertEquals(HttpServletResponse.SC_SEE_OTHER, res.getStatus());
    //J-
    assertEquals(
      new URI("https://www.scm-manager.org"), 
      res.getOutputHeaders().getFirst("Location")
    );
    //J+
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Rule
  public ShiroRule shiroRule = new ShiroRule();

  /** Field description */
  @Mock
  private CasConfiguration cas;
}
