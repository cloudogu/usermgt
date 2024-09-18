package de.triology.universeadm.user;

import de.triology.universeadm.AbstractEntityEvent;
import de.triology.universeadm.EventType;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
