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

package de.triology.universeadm.group;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.triology.universeadm.validation.RDN;
import java.util.List;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
    if (members == null)
    {
      members = Lists.newArrayList();
    }
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

  @RDN
  private String name;

  private String description;

  private List<String> members;
}
