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
public class SettingsException extends RuntimeException
{

  public SettingsException()
  {
  }

  public SettingsException(String message)
  {
    super(message);
  }

  public SettingsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public SettingsException(Throwable cause)
  {
    super(cause);
  }
  
}
