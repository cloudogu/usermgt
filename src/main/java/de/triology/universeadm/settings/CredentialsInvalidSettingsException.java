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
public class CredentialsInvalidSettingsException extends SettingsException
{

  public CredentialsInvalidSettingsException()
  {
  }

  public CredentialsInvalidSettingsException(String message)
  {
    super(message);
  }

  public CredentialsInvalidSettingsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public CredentialsInvalidSettingsException(Throwable cause)
  {
    super(cause);
  }
  
}
