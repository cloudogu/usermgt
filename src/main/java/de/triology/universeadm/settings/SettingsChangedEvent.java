/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.settings;

import com.google.common.base.Objects;

/**
 *
 * @author ssdorra
 */
public class SettingsChangedEvent
{

  private final Settings settings;
  private final Settings oldSettings;

  public SettingsChangedEvent(Settings settings, Settings oldSettings)
  {
    this.settings = settings;
    this.oldSettings = oldSettings;
  }

  public Settings getOldSettings()
  {
    return oldSettings;
  }

  public Settings getSettings()
  {
    return settings;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(settings, oldSettings);
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
    final SettingsChangedEvent other = (SettingsChangedEvent) obj;
    return Objects.equal(settings, other.settings) 
      && Objects.equal(oldSettings, other.oldSettings);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
                  .add("settings", settings)
                  .add("oldSettings", oldSettings)
                  .toString();
  }

}
