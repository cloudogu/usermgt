package de.triology.universeadm.mapping;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Provider
public class IllegalQueryExceptionMapper
  implements ExceptionMapper<IllegalQueryException>
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(IllegalQueryExceptionMapper.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param exception
   *
   * @return
   */
  @Override
  public Response toResponse(IllegalQueryException exception)
  {
    logger.debug("map IllegalArgumentException to status code 400");

    if (logger.isTraceEnabled())
    {
      logger.trace("bad request", exception);
    }

    return Response.status(Response.Status.BAD_REQUEST).build();
  }
}
