/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import java.io.IOException;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

/**
 *
 * @author ssdorra
 */
public final class Resources
{

  private Resources(){}
  
  private static final ObjectMapper mapper = new ObjectMapper();
  
  public static MockHttpResponse dispatch(Object resource, MockHttpRequest request) throws IOException
  {
    return dispatch(resource, request, null);
  }
  
  public static MockHttpResponse dispatch(Object resource, MockHttpRequest request, Object object) throws IOException {
    Dispatcher dispatcher = createDispatcher(resource);
    MockHttpResponse response = new MockHttpResponse();
    if ( object != null ){
      request.content(mapper.writeValueAsBytes(object));
      request.contentType(MediaType.APPLICATION_JSON_TYPE);
    }
    dispatcher.invoke(request, response);
    return response;
  }
  
  public static Dispatcher createDispatcher(Object resource)
  {
    Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
    dispatcher.getRegistry().addSingletonResource(resource);
    return dispatcher;
  }
  
  public static JsonNode parseJson(MockHttpResponse response) throws IOException
  {
    JsonFactory factory = mapper.getJsonFactory();
    JsonParser jp = factory.createJsonParser(response.getOutput());
    return mapper.readTree(jp);
  }
  
}