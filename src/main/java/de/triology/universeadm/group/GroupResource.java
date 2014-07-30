/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.group;

import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import javax.ws.rs.Path;

/**
 *
 * @author ssdorra
 */
@Path("groups")
public class GroupResource extends AbstractManagerResource<Group>
{

  @Inject
  public GroupResource(GroupManager groupManager)
  {
    super(groupManager);
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
  
}
