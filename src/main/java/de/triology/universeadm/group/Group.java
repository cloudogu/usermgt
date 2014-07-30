/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.group;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;

/**
 *
 * @author ssdorra
 */
public class Group implements Comparable<Group>
{

  public Group()
  {
  }

  public Group(String name)
  {
    this.name = name;
  }

  public Group(String name, String description, List<String> members)
  {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  public Group(String name, String description, String... members)
  {
    this.name = name;
    this.description = description;
    this.members = Lists.newArrayList(members);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public List<String> getMembers()
  {
    return members;
  }

  public void setMembers(List<String> members)
  {
    this.members = members;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(name, description, members);
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
    final Group other = (Group) obj;
    return Objects.equal(name, other.name) 
      && Objects.equal(description, other.description) 
      && Objects.equal(members, other.members);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("name", name)
                  .add("description", description)
                  .add("members", members)
                  .toString();
  }

  @Override
  public int compareTo(Group o)
  {
    return Strings.nullToEmpty(name).compareTo(Strings.nullToEmpty(o.name));
  }

  private String name;

  private String description;

  private List<String> members;
}
