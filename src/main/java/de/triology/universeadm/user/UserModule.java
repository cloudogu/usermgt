/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.user;

import com.google.inject.AbstractModule;

/**
 *
 * @author ssdorra
 */
public class UserModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(UserManager.class).to(LDAPUserManager.class);
    bind(MemberListener.class).asEagerSingleton();
    bind(UserResource.class);
  }
  
}
