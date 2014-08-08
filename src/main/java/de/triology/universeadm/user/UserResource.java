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

package de.triology.universeadm.user;


import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("users")
public class UserResource extends AbstractManagerResource<User>
{

  private final UserManager userManager;
  
  @Inject
  public UserResource(UserManager userManager)
  {
    super(userManager);
    this.userManager = userManager;
  }

  @Override
  protected String getId(User user)
  {
    return user.getUsername();
  }

  @Override
  protected void prepareForModify(String id, User user)
  {
    user.setUsername(id);
  }
  
  @POST
  @Path("{user}/groups/{group}")
  public Response addMembership(@PathParam("user") String username, @PathParam("group") String group)
  {
    Response.ResponseBuilder builder;
    
    User user = userManager.get(username);
    if ( user == null )
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    else if ( user.getMemberOf().contains(group) )
    {
      builder = Response.status(Response.Status.CONFLICT);
    }
    else 
    {
      user.getMemberOf().add(group);
      userManager.modify(user);
      builder = Response.noContent();
    }
    
    return builder.build();
  }
  
    
  @DELETE
  @Path("{user}/groups/{group}")
  public Response removeMember(@PathParam("user") String username, @PathParam("group") String group)
  {
    Response.ResponseBuilder builder;
    
    User user = userManager.get(username);
    if ( user == null )
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    else if ( ! user.getMemberOf().contains(group) )
    {
      builder = Response.status(Response.Status.CONFLICT);
    }
    else 
    {
      user.getMemberOf().remove(group);
      userManager.modify(user);
      builder = Response.noContent();
    }
    
    return builder.build();
  }

}
