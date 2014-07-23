/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.scmmu.usermgm;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

/**
 *
 * @author ssdorra
 */
public class BootstrapContextListener extends GuiceResteasyBootstrapServletContextListener
{

  @Override
  protected List<? extends Module> getModules(ServletContext context)
  {
    return ImmutableList.of(
      ShiroWebModule.guiceFilterModule(),
      new MainModule(),
      new SecurityModule(context)
    );
  }

}
