/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
 */

package de.triology.universeadm;

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
  configuration = "classpath:de/triology/universeadm/shiro.001.ini"
)
public class SubjectResourceTest
{

  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void testSubject() throws URISyntaxException, IOException
  {
    MockHttpRequest req = MockHttpRequest.get("/subject");
    MockHttpResponse res = Resources.dispatch(new SubjectResource(), req);
    JsonNode node = Resources.parseJson(res);
    assertEquals("trillian", node.path("principal").asText());
    assertTrue(node.path("admin").asBoolean());
  }
  
  @Test
  public void testSubjectUnauthenticated() throws URISyntaxException, IOException{
    MockHttpRequest req = MockHttpRequest.get("/subject");
    MockHttpResponse res = Resources.dispatch(new SubjectResource(), req);
    assertEquals(HttpServletResponse.SC_FORBIDDEN, res.getStatus());
  }
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
  
}
