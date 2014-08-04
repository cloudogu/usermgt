/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.group;

import de.triology.universeadm.AbstractEntityEvent;
import de.triology.universeadm.EventType;

/**
 *
 * @author ssdorra
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
