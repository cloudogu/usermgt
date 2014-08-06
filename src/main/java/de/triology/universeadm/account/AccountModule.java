/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.account;

import com.google.inject.AbstractModule;

/**
 *
 * @author ssdorra
 */
public class AccountModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(AccountManager.class).to(DefaultAccountManager.class);
    bind(AccountResource.class);
  }
  
}
