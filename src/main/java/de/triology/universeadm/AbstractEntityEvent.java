/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import com.google.common.base.Objects;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public class AbstractEntityEvent<T> implements EntityEvent<T>
{

  private final T entity;
  private final T oldEntity;
  private final EventType type;

  public AbstractEntityEvent(T entity, T oldEntity)
  {
    this.entity = entity;
    this.oldEntity = oldEntity;
    this.type = EventType.MODIFY;
  }
  
  public AbstractEntityEvent(T entity, EventType type)
  {
    this.entity = entity;
    this.oldEntity = null;
    this.type = type;
  }
  
  @Override
  public EventType getType()
  {
    return type;
  }

  @Override
  public T getEntity()
  {
    return entity;
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hashCode(entity, oldEntity, type);
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
    final AbstractEntityEvent<T> other = (AbstractEntityEvent<T>) obj;
    return Objects.equal(entity, other.entity)
            && Objects.equal(oldEntity, other.oldEntity)
            && Objects.equal(type, other.type);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
            .add("entity", entity)
            .add("oldEntity", oldEntity)
            .add("type", type)
            .toString();
  }
  
}
