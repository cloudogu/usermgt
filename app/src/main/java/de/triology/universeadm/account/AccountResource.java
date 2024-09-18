package de.triology.universeadm.account;

import com.google.inject.Inject;
import de.triology.universeadm.*;
import de.triology.universeadm.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.triology.universeadm.user.imports.FieldConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("account")
public class AccountResource
{
  
  private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);
  
  private final AccountManager accountManager;

  @Inject
  public AccountResource(AccountManager accountManager)
  {
    this.accountManager = accountManager;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAccount()
  {
    Response.ResponseBuilder builder;
    
    User account = accountManager.getCurrentUser();
    if ( account != null ){
      builder = Response.ok(account);
    } else {
      logger.error("call /api/account without prior authentication");
      builder = Response.status(Response.Status.FORBIDDEN);
    }
    
    return builder.build();
  }

  @GET
  @Path("passwordpolicy")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getConfig()
  {
    Response.ResponseBuilder builder;
    User account = accountManager.getCurrentUser();
    if ( account != null ){
      final Configuration conf = Configuration.getInstance();
      builder = Response.ok(conf.getContent(), MediaType.APPLICATION_JSON);
    } else {
      logger.error("call /api/account/passwordpolicy without prior authentication");
      builder = Response.status(Response.Status.FORBIDDEN);
    }

    return builder.build();
  }

  @GET
  @Path("gui_config")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getGuiConfig()
  {
    Response.ResponseBuilder builder;
    User account = accountManager.getCurrentUser();
    if ( account != null ){
      final Configuration conf = Configuration.getInstance();
      builder = Response.ok(conf.getGuiContent(), MediaType.APPLICATION_JSON);
    } else {
      logger.error("call /api/account/gui_config without prior authentication");
      builder = Response.status(Response.Status.FORBIDDEN);
    }

    return builder.build();
  }
  
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response modifyAccount(User user){
    try {
      accountManager.modifyCurrentUser(user);
    }
    catch (UniqueConstraintViolationException e){
      return Response.status(Response.Status.CONFLICT).entity(new UniqueConstraintViolationResponse(e)).build();
    }
    catch (FieldConstraintViolationException e){
      return Response.status(Response.Status.CONFLICT).entity(new FieldConstraintViolationResponse(e)).build();
    }
    catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(new DenyChangeUserResponse(e)).build();
    }
    return Response.noContent().build();
  }
  
}
