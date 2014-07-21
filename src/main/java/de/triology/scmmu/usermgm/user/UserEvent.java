/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.scmmu.usermgm.user;

import com.google.common.base.Objects;
import de.triology.scmmu.usermgm.EventType;

/**
 *
 * @author ssdorra
 */
public class UserEvent
{

  private final User user;
  private final EventType type;

  public UserEvent(User user, EventType type)
  {
    this.user = user;
    this.type = type;
  }

  public EventType getType()
  {
    return type;
  }

  public User getUser()
  {
    return user;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(user, type);
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
    final UserEvent other = (UserEvent) obj;
    return Objects.equal(user, other.user)
            && Objects.equal(type, other.type);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
            .add("user", user)
            .add("type", type)
            .toString();
  }

}
