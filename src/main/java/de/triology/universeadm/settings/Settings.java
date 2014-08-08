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

package de.triology.universeadm.settings;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@XmlRootElement(name = "settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class Settings
{
  
  private Credentials updateServiceCredentials;
  
  private boolean updateCheckEnabled;
  
  private boolean updateBugzillaPlugin;
  
  private boolean updateCasServer;

  public Settings()
  {
  }

  public Settings(Credentials updateServiceCredentials, boolean updateCheckEnabled, boolean updateBugzillaPlugin, boolean updateCasServer)
  {
    this.updateServiceCredentials = updateServiceCredentials;
    this.updateCheckEnabled = updateCheckEnabled;
    this.updateBugzillaPlugin = updateBugzillaPlugin;
    this.updateCasServer = updateCasServer;
  }

  public Credentials getUpdateServiceCredentials()
  {
    return updateServiceCredentials;
  }

  public boolean isUpdateCheckEnabled()
  {
    return updateCheckEnabled;
  }

  public boolean isUpdateBugzillaPlugin()
  {
    return updateBugzillaPlugin;
  }

  public boolean isUpdateCasServer()
  {
    return updateCasServer;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(updateServiceCredentials, updateCheckEnabled, 
            updateBugzillaPlugin, updateCasServer);
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
    final Settings other = (Settings) obj;
    return Objects.equal(updateServiceCredentials, other.updateServiceCredentials)
      && Objects.equal(updateCheckEnabled, other.updateCheckEnabled)
      && Objects.equal(updateBugzillaPlugin, other.updateBugzillaPlugin)
      && Objects.equal(updateCasServer, other.updateCasServer);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("updateServiceCredentials", updateServiceCredentials)
                  .add("updateCheckEnabled", updateCheckEnabled)
                  .add("updateBugzillaPlugin", updateBugzillaPlugin)
                  .add("updateCasServer", updateCasServer)
                  .toString();
  }
  
}
