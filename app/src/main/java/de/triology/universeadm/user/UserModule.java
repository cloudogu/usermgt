package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.kohsuke.MetaInfServices;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@MetaInfServices(Module.class)
public class UserModule extends AbstractModule
{

  /**
   * Method description
   *
   */
  @Override
  protected void configure()
  {
    bind(UserManager.class).to(LDAPUserManager.class);
    bind(MemberListener.class).asEagerSingleton();
    bind(UserResource.class);
    bind(UserSelfRemoveExceptionMapper.class);

  }
}
