/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ssdorra
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
