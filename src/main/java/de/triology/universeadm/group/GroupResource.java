/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.group;

import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author ssdorra
 */
@Path("groups")
public class GroupResource extends AbstractManagerResource<Group>
{

  private final GroupManager groupManager;
  
  @Inject
  public GroupResource(GroupManager groupManager)
  {
    super(groupManager);
    this.groupManager = groupManager;
  }

  @Override
  protected String getId(Group group)
  {
    return group.getName();
  }

  @Override
  protected void prepareForModify(String id, Group group)
  {
    group.setName(id);
  }
  
  @POST
  @Path("{name}/members/{member}")
  public Response addMember(@PathParam("name") String name, @PathParam("member") String member)
  {
    Response.ResponseBuilder builder;
    
    Group group = groupManager.get(name);
    if ( group == null )
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    else if ( group.getMembers().contains(member) )
    {
      builder = Response.status(Response.Status.CONFLICT);
    }
    else 
    {
      group.getMembers().add(member);
      groupManager.modify(group);
      builder = Response.noContent();
    }
    
    return builder.build();
  }
  
  @DELETE
  @Path("{name}/members/{member}")
  public Response removeMember(@PathParam("name") String name, @PathParam("member") String member)
  {
    Response.ResponseBuilder builder;
    
    Group group = groupManager.get(name);
    if ( group == null )
    {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    else if ( ! group.getMembers().contains(member) )
    {
      builder = Response.status(Response.Status.CONFLICT);
    }
    else 
    {
      group.getMembers().remove(member);
      groupManager.modify(group);
      builder = Response.noContent();
    }
    
    return builder.build();
  }
  
}
