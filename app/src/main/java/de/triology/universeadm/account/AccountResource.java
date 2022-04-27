/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */

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
    catch (ConstraintViolationException e){
      return Response.status(Response.Status.CONFLICT).entity(new ConstraintViolationResponse(e)).build();
    }
    catch (IllegalArgumentException e) {
      return Response.status(Response.Status.BAD_REQUEST).entity(new DenyChangeUserResponse(e)).build();
    }
    return Response.noContent().build();
  }
  
}
