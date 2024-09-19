package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
@SubjectAware(configuration = "classpath:de/triology/universeadm/shiro.001.ini")
public class ApiAuthenticationFilterTest
{

  /** Field description */
  private static final String UA_APICLIENT = "UniverseADM API-Client";

  /** Field description */
  private static final String UA_CHROME =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public ApiAuthenticationFilterTest() {}

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Test
  public void testSendChallengeApiClient() throws IOException
  {
    when(request.getHeader(
      ApiAuthenticationFilter.HEADER_USERAGENT)).thenReturn(UA_APICLIENT);
    filter.sendChallenge(request, response);
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response).setHeader("WWW-Authenticate",
      "BASIC realm=\"application\"");
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Test
  public void testSendChallengeBrowser() throws IOException
  {
    when(request.getHeader(
      ApiAuthenticationFilter.HEADER_USERAGENT)).thenReturn(UA_CHROME);
    filter.sendChallenge(request, response);
    verify(response).sendRedirect(any());
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Rule
  public ShiroRule shiro = new ShiroRule();

  /** Field description */
  private final ApiAuthenticationFilter filter = new ApiAuthenticationFilter();

  /** Field description */
  @Mock
  private HttpServletRequest request;

  /** Field description */
  @Mock
  private HttpServletResponse response;
}
