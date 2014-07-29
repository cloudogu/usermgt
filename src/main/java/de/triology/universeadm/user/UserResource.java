/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;


import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import javax.ws.rs.Path;

/**
 *
 * @author Sebastian Sdorra
 */
@Path("users")
public class UserResource extends AbstractManagerResource<User>
{

  @Inject
  public UserResource(UserManager userManager)
  {
    super(userManager);
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

}
