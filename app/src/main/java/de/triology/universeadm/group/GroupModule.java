package de.triology.universeadm.group;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.kohsuke.MetaInfServices;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@MetaInfServices(Module.class)
public class GroupModule extends AbstractModule
{

  /**
   * Method description
   *
   */
  @Override
  protected void configure()
  {
    bind(GroupManager.class).to(LDAPGroupManager.class);
    bind(MemberOfListener.class).asEagerSingleton();
    bind(GroupResource.class);
    bind(UndeletableGroupManager.class);
  }
}
