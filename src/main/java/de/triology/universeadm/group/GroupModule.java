/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.group;

import com.google.inject.AbstractModule;

/**
 *
 * @author ssdorra
 */
public class GroupModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(GroupManager.class).to(LDAPGroupManager.class);
    bind(MemberOfListener.class).asEagerSingleton();
    bind(GroupResource.class);
  }

}
