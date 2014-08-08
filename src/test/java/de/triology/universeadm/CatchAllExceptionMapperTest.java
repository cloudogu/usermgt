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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
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
public class CatchAllExceptionMapperTest
{
  
  private static final TestLogger logger = TestLoggerFactory.getTestLogger(CatchAllExceptionMapper.class);

  @Test
  public void testValid() throws URISyntaxException, IOException
  {
    MockHttpResponse response = execute("/ex/valid");
    assertEquals(204, response.getStatus());
    assertThat(logger.getLoggingEvents(), empty());
  }
  
  @Test
  public void testInvalid() throws URISyntaxException, IOException
  {
    MockHttpResponse response = execute("/ex/invalid");
    assertEquals(500, response.getStatus());
    List<LoggingEvent> events = logger.getLoggingEvents();
    assertFalse(events.isEmpty());
    assertEquals(1, events.size());
    assertEquals("unhandled exception", events.get(0).getMessage());
    assertEquals("invalid resource called", events.get(0).getThrowable().get().getMessage());
  }
  
  @Rule
  public TestLoggerFactoryResetRule resetRule = new TestLoggerFactoryResetRule();
  
  private MockHttpResponse execute(String path) throws URISyntaxException, IOException
  {
    Dispatcher dispatcher = Resources.createDispatcher(new ExceptionResource());
    dispatcher.getProviderFactory().register(CatchAllExceptionMapper.class);
    MockHttpRequest request = MockHttpRequest.create("GET", path);
    return Resources.dispatch(dispatcher,request, null);
  }
  
  @Path("ex")
  public static class ExceptionResource {
    
    @GET
    @Path("valid")
    public void valid()
    {
      
    }
    
    @GET
    @Path("invalid")
    public void invalid()
    {
      throw new IllegalArgumentException("invalid resource called");
    }
    
  }
  
}
