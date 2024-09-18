package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import de.triology.universeadm.RestError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map {@link UserSelfRemoveException} to http status code 409 (Conflict).
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @since 1.1.0
 */
@Provider
public class UserSelfRemoveExceptionMapper
  implements ExceptionMapper<UserSelfRemoveException>
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(UserSelfRemoveExceptionMapper.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Return status code 409.
   *
   *
   * @param exception user self remove exception
   *
   * @return response with status code 409
   */
  @Override
  public Response toResponse(UserSelfRemoveException exception)
  {
    StringBuilder buffer = new StringBuilder("the user ");

    buffer.append(exception.getPrincipal());
    buffer.append(" has tried to remove himself");

    String message = buffer.toString();

    logger.warn(message);

    return Response.status(Response.Status.CONFLICT).type(
      MediaType.APPLICATION_JSON_TYPE).entity(new RestError(message)).build();
  }
}
