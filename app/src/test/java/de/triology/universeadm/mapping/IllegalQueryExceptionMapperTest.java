package de.triology.universeadm.mapping;

//~--- non-JDK imports --------------------------------------------------------

import de.triology.universeadm.Resources;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

import org.junit.Test;

import static org.junit.Assert.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class IllegalQueryExceptionMapperTest
{

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testMapping() throws URISyntaxException, IOException
  {
    MockHttpResponse response = dispatch("/a/ex");

    assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  @Test
  public void testNoMapping() throws IOException, URISyntaxException
  {
    MockHttpResponse response = dispatch("/a");

    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    assertEquals("a", response.getContentAsString());
  }

  /**
   * Method description
   *
   *
   * @param path
   *
   * @return
   *
   * @throws IOException
   * @throws URISyntaxException
   */
  private MockHttpResponse dispatch(String path)
    throws IOException, URISyntaxException
  {
    Dispatcher dispatcher = Resources.createDispatcher(new Resource());

    dispatcher.getProviderFactory().register(IllegalQueryExceptionMapper.class);

    return Resources.dispatch(dispatcher, MockHttpRequest.get(path), null);
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/08/27
   * @author         Enter your name here...    
   */
  @Path("a")
  public static class Resource
  {

    /**
     * Method description
     *
     *
     * @return
     */
    @GET
    @Path("ex")
    public String withException()
    {
      throw new IllegalQueryException();
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @GET
    public String withoutException()
    {
      return "a";
    }
  }
}
