package de.triology.universeadm.group;

import de.triology.universeadm.AbstractEntityEvent;
import de.triology.universeadm.EventType;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class GroupEvent extends AbstractEntityEvent<Group>
{
  
  public GroupEvent(Group entity, Group oldEntity)
  {
    super(entity, oldEntity);
  }

  public GroupEvent(Group entity, EventType type)
  {
    super(entity, type);
  }
 
}
