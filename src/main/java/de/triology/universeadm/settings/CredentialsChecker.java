/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import java.io.IOException;

/**
 *
 * @author ssdorra
 */
public interface CredentialsChecker
{
 
  public boolean checkCredentials(Credentials credentials, String checkUrl) throws IOException;
}
