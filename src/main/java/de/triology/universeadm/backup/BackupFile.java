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

package de.triology.universeadm.backup;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import de.triology.universeadm.XmlDateTimeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class BackupFile implements Comparable<BackupFile>
{
  
  private String name;
  @XmlJavaTypeAdapter(XmlDateTimeAdapter.class)
  private DateTime lastModified;
  private long size;

  public BackupFile()
  {
  }

  public BackupFile(String name, DateTime lastModified, long size)
  {
    this.name = name;
    this.lastModified = lastModified;
    this.size = size;
  }

  public String getName()
  {
    return name;
  }

  public DateTime getLastModified()
  {
    return lastModified;
  }

  public long getSize()
  {
    return size;
  }
  
  @Override
  public int compareTo(BackupFile o)
  {
    return Strings.nullToEmpty(name).compareTo(Strings.nullToEmpty(o.name));
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(name, lastModified, size);
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
    final BackupFile other = (BackupFile) obj;
    return Objects.equal(name, other.name) 
      && Objects.equal(lastModified, other.lastModified) 
      && Objects.equal(size, other.size);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("name", name)
                  .add("lastModified", lastModified)
                  .add("size", size)
                  .toString();
  }
  
}
