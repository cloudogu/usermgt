/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

import de.triology.universeadm.AbstractEntityEvent;
import de.triology.universeadm.EventType;

/**
 *
 * @author ssdorra
 */
public class UserEvent extends AbstractEntityEvent<User>
{

  public UserEvent(User entity, User oldEntity)
  {
    super(entity, oldEntity);
  }

  public UserEvent(User entity, EventType type)
  {
    super(entity, type);
  }

}
