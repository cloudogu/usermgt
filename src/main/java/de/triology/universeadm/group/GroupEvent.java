/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.group;

import com.google.common.base.Objects;
import de.triology.universeadm.EntityEvent;
import de.triology.universeadm.EventType;

/**
 *
 * @author ssdorra
 */
public class GroupEvent implements EntityEvent<Group>
{
  private final Group entity;
  private final EventType type;

  public GroupEvent(Group entity, EventType type)
  {
    this.entity = entity;
    this.type = type;
  }

  @Override
  public Group getEntity()
  {
    return entity;
  }

  @Override
  public EventType getType()
  {
    return type;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(entity, type);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final GroupEvent other = (GroupEvent) obj;
    return Objects.equal(entity, other.entity) 
            && Objects.equal(type, other.type);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("entity", entity)
                  .add("type", type)
                  .toString();
  }
  
}
