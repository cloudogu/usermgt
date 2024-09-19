package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

//~--- JDK imports ------------------------------------------------------------

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("logout")
public class LogoutResource
{

  /**
   * Constructs ...
   *
   *
   * @param casConfiguration
   */
  @Inject
  public LogoutResource(CasConfiguration casConfiguration)
  {
    this.casConfiguration = casConfiguration;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   *
   * @throws URISyntaxException
   */
  @GET
  public Response logout() throws URISyntaxException
  {
    Subject subject = SecurityUtils.getSubject();

    if (subject.isAuthenticated())
    {
      subject.logout();
    }

    return Response.seeOther(new URI(casConfiguration.getLogoutUrl())).build();
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final CasConfiguration casConfiguration;
}
