package de.triology.universeadm;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Provider
public class CatchAllExceptionMapper implements ExceptionMapper<Throwable>
{

  private static final Logger logger = LoggerFactory.getLogger(CatchAllExceptionMapper.class);

  @Override
  public Response toResponse(Throwable exception)
  {
    logger.error("unhandled exception", exception);
    return Response.serverError().build();
  }

}
