/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import com.github.legman.Subscribe;
import com.google.inject.Singleton;

/**
 * /etc/scmmu
 * - scmbugplug
 * - scmcasupdt
 * - scmcreds (leerzeile invalid, username password valid)
 * - 
 * 
 * @author ssdorra
 */
@Singleton
public class SettingsEventHandler
{
  
  @Subscribe
  public void handleSettingsEvent(SettingsChangedEvent event)
  {
    
  }
  
}
