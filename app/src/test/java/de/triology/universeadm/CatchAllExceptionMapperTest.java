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
