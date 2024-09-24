package de.triology.universeadm;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Provider
public class DisableCacheResponseFilter implements ContainerResponseFilter
{

  /**
   * Method description
   *
   *
   * @param requestContext
   * @param responseContext
   *
   * @throws IOException
   */
  @Override
  public void filter(ContainerRequestContext requestContext,
    ContainerResponseContext responseContext)
    throws IOException
  {
    responseContext.getHeaders().putSingle("Cache-Control",
      "no-cache, no-store, must-revalidate");
    responseContext.getHeaders().putSingle("Pragma", "no-cache");
    responseContext.getHeaders().putSingle("Expires", 0);
    
  }
}
