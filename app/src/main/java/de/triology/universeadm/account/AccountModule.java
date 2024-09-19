package de.triology.universeadm.account;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.kohsuke.MetaInfServices;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@MetaInfServices(Module.class)
public class AccountModule extends AbstractModule
{

  /**
   * Method description
   *
   */
  @Override
  protected void configure()
  {
    bind(AccountManager.class).to(DefaultAccountManager.class);
    bind(AccountResource.class);
  }
}
