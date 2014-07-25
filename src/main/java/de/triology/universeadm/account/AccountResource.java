/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.account;

import com.google.inject.Inject;
import de.triology.universeadm.user.User;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
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
  
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  public Response modifyAccount(User user){
    accountManager.modifyCurrentUser(user);
    return Response.noContent().build();
  }
  
}
