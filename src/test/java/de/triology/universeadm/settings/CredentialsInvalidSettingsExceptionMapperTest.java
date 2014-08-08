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

package de.triology.universeadm.settings;

import de.triology.universeadm.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import static org.hamcrest.Matchers.*;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;
/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class CredentialsInvalidSettingsExceptionMapperTest
{

  private static final TestLogger logger = TestLoggerFactory.getTestLogger(CredentialsInvalidSettingsExceptionMapper.class);
  
  @Test
  public void testExceptionMapping() throws URISyntaxException, IOException
  {
    MockHttpResponse res = dispatch("/test/exception");
    List<LoggingEvent> events = logger.getLoggingEvents();
    assertEquals(1, events.size());
    assertEquals("invalid credentials entered", events.get(0).getMessage());
    assertEquals(HttpServletResponse.SC_BAD_REQUEST, res.getStatus());
  }
  
  @Test
  public void testExceptionMappingWithoutException() throws URISyntaxException, IOException
  {
    MockHttpResponse res = dispatch("/test");
    assertThat(logger.getLoggingEvents(), empty());
    assertEquals(HttpServletResponse.SC_NO_CONTENT, res.getStatus());
  }
  
  private MockHttpResponse dispatch(String path) throws URISyntaxException, IOException{
   MockHttpRequest req = MockHttpRequest.get(path);
    Dispatcher dispatcher = Resources.createDispatcher(new MappingResource());
    dispatcher.getProviderFactory().register(CredentialsInvalidSettingsExceptionMapper.class);
    return Resources.dispatch(dispatcher, req, null);
  }
  
  @Rule
  public TestLoggerFactoryResetRule resetRule = new TestLoggerFactoryResetRule();
  
  @Path("test")
  public static class MappingResource {
    
    @GET
    public void test(){}
    
    @GET
    @Path("exception")
    public void exception(){
      throw new CredentialsInvalidSettingsException();
    }
  }
  
}
