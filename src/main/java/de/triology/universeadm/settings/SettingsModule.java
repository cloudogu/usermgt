/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import com.google.inject.AbstractModule;

/**
 *
 * @author ssdorra
 */
public class SettingsModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(CredentialsChecker.class).to(DefaultCredentialsChecker.class);
    bind(SettingsStore.class).to(DefaultSettingsStore.class);
    bind(SettingsResource.class);
    bind(CredentialsInvalidSettingsExceptionMapper.class);
  }
  
}
