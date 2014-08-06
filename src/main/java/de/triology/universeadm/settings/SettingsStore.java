/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.settings;

/**
 *
 * @author ssdorra
 */
public interface SettingsStore
{

  public void set(Settings settings);

  public Settings get();
}
