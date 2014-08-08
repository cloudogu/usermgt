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
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Resources
{

  private Resources(){}
  
  private static final ObjectMapper mapper = new ObjectMapper().setAnnotationIntrospector(new JaxbAnnotationIntrospector());
  
  public static MockHttpResponse dispatch(Object resource, MockHttpRequest request) throws IOException
  {
    return dispatch(resource, request, null);
  }
  
  public static MockHttpResponse dispatch(Object resource, MockHttpRequest request, Object object) throws IOException {
    Dispatcher dispatcher = createDispatcher(resource);
    return dispatch(dispatcher, request, object);
  }
  
  public static MockHttpResponse dispatch(Dispatcher dispatcher, MockHttpRequest request, Object object) throws IOException {
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
  
  public static <T> T parseJson(MockHttpResponse response, Class<T> objectClass) throws IOException {
    return mapper.readValue(response.getContentAsString(), objectClass);
  }
  
  public static JsonNode parseJson(MockHttpResponse response) throws IOException
  {
    JsonFactory factory = mapper.getJsonFactory();
    JsonParser jp = factory.createJsonParser(response.getOutput());
    return mapper.readTree(jp);
  }
  
}
