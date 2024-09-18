package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.collect.ImmutableMap;
import com.google.inject.servlet.ServletModule;
import java.util.Map;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class LDAPDisabledModule extends ServletModule
{

  /**
   * Method description
   *
   */
  @Override
  protected void configureServlets()
  {
    serve("/components/*", "/scripts/*", "/style/*")
      .with(ResourceServlet.class);

    Map<String, String> initParams = ImmutableMap.of(
      RedirectServlet.PARAM_PATH, "/error/ldap-disabled.html"
    );
    // serve index pages
    serve("/", "/index.html", "/index-debug.html")
      .with(RedirectServlet.class, initParams);
  }
}
