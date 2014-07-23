/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm;

import com.google.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author ssdorra
 */
@Path("logout")
public class LogoutResource
{
  
  private final CasConfiguration casConfiguration;

  @Inject
  public LogoutResource(CasConfiguration casConfiguration)
  {
    this.casConfiguration = casConfiguration;
  }
  
  @GET
  public Response logout() throws URISyntaxException
  {
    SecurityUtils.getSubject().logout();
    return Response.seeOther(new URI(casConfiguration.getLogoutUrl())).build();
  }
}
