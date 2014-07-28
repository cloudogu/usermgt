/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------
import com.google.common.base.Strings;
import com.google.inject.Inject;
import de.triology.universeadm.PagedResultList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra
 */
@Path("users")
public class UserResource
{

  /**
   * the logger for UserResource
   */
  private static final Logger logger
          = LoggerFactory.getLogger(UserResource.class);

  //~--- constructors ---------------------------------------------------------
  /**
   * Constructs ...
   *
   *
   * @param userManager
   */
  @Inject
  public UserResource(UserManager userManager)
  {
    this.userManager = userManager;
  }

  //~--- methods --------------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param uriInfo
   * @param user
   *
   * @return
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(@Context UriInfo uriInfo, User user)
  {
    ResponseBuilder builder;

    try
    {
      userManager.create(user);

      UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());

      uriBuilder.path(user.getUsername());
      builder = Response.created(uriBuilder.build());
    }
    catch (UserAlreadyExistsException ex)
    {
      logger.warn("user {} already exists", user.getUsername());
      builder = Response.status(Response.Status.CONFLICT);
    }

    return builder.build();
  }

  /**
   * Method description
   *
   *
   * @param username
   * @param user
   *
   * @return
   */
  @PUT
  @Path("{username}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response modify(@PathParam("username") String username, User user)
  {
    user.setUsername(username);
    userManager.modify(user);

    return Response.noContent().build();
  }

  /**
   * Method description
   *
   *
   * @param username
   *
   * @return
   */
  @DELETE
  @Path("{username}")
  public Response remove(@PathParam("username") String username)
  {
    Response.ResponseBuilder builder;
    User user = userManager.get(username);

    if (user == null)
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    else
    {
      userManager.remove(user);
      builder = Response.noContent();
    }

    return builder.build();
  }

  //~--- get methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param username
   *
   * @return
   */
  @GET
  @Path("{username}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response get(@PathParam("username") String username)
  {
    User user = userManager.get(username);
    ResponseBuilder builder;
    if (user != null)
    {
      builder = Response.ok(user);
    }
    else
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    return builder.build();
  }

  /**
   * Method description
   *
   *
   * @param start
   * @param limit
   * @param query
   * @return
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll(@QueryParam("start") int start, @QueryParam("limit") int limit, @QueryParam("query") String query)
  {
    if (start < 0)
    {
      start = 0;
    }
    
    if (limit <= 0 || limit > 1000)
    {
      limit = 20;
    }
    
    PagedResultList<User> result;
    if (Strings.isNullOrEmpty(query))
    {
      result = userManager.getAll(start, limit);
    }
    else
    {
      result = userManager.search(query, start, limit);
    }
    
    ResponseBuilder builder;
    if (result != null)
    {
      builder = Response.ok(result);
    }
    else if (Strings.isNullOrEmpty(query))
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    } 
    else 
    {
      builder = Response.noContent();
    }
    return builder.build();
  }

  //~--- fields ---------------------------------------------------------------
  /**
   * Field description
   */
  private final UserManager userManager;
}
