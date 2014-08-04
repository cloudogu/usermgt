/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Sebastian Sdorra
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
