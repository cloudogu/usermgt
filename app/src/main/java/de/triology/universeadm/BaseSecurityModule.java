package de.triology.universeadm;

import javax.servlet.ServletContext;
import org.apache.shiro.guice.web.ShiroWebModule;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public abstract class BaseSecurityModule extends ShiroWebModule
{

  protected BaseSecurityModule(ServletContext context)
  {
    super(context);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected void configureShiroWeb()
  {
    addFilterChain("/error/*", ANON);
    addFilterChain("/style/**", ANON);
    addFilterChain("/components/**", ANON);
    addFilterChain("/api/logout", ANON);
    configureRealm();
  }
  
  protected abstract void configureRealm();
  
}
