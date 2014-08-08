/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */

package de.triology.universeadm;

import com.google.common.base.Objects;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
  public T getOldEntity()
  {
    return oldEntity;
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
