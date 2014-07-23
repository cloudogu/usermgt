/*mvn -DskipTests -P'!webcomponents' package jetty:run
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm;

import com.google.common.base.Joiner;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

/**
 *
 * @author ssdorra
 */
@Path("subject")
public class SubjectResource
{
  
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getSubject()
  {
    Subject subject = SecurityUtils.getSubject();
    String principals = Joiner.on(", ").join(subject.getPrincipals().asList());
    StringBuilder buffer = new StringBuilder("Subject\n");
    buffer.append("\nprincipal : ").append(subject.getPrincipal().toString());
    buffer.append("\nprincipals: ").append(principals);
    buffer.append("\nadmin     : ").append(subject.hasRole("admins"));
    return buffer.toString();
  }
  
}
