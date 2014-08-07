/*mvn -DskipTests -P'!webcomponents' package jetty:run
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Path("subject")
public class SubjectResource
{
  
  private static final Logger logger = LoggerFactory.getLogger(SubjectResource.class);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSubject()
  {
    Subject subject = SecurityUtils.getSubject();
    Response.ResponseBuilder builder;
    if (subject.isAuthenticated())
    {
      Map<String, Object> attributes = Maps.newHashMap();
      Map<String, Object> principals = (Map<String, Object>) subject.getPrincipals().oneByType(Map.class);
      if ( principals != null )
      {
        attributes.putAll(principals);
      } 
      else 
      {
        logger.warn("no principals available in subject");
      }
      attributes.put("principal", subject.getPrincipal());
      attributes.put("admin", subject.hasRole(Roles.ADMINISTRATOR));
      builder = Response.ok(attributes);
    }
    else
    {
      logger.error("call to /api/subject without authentication");
      builder = Response.status(Response.Status.FORBIDDEN);
    }
    return builder.build();
  }
}
