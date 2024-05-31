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

package de.triology.universeadm.validation;

import de.triology.universeadm.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.validation.Validation;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class HibernateValidatorExceptionMappingTest
{

  @Test
  public void testValid() throws URISyntaxException, IOException
  {
    MockHttpResponse response = validate(new VObject("dent"));
    assertEquals(204, response.getStatus());
  }

  @Test
  public void validWithLongEmail() throws IOException, URISyntaxException
  {
    MockHttpResponse response = validate(new VObject("trillian.mcmillan.ford.prefect.arthur.dent@hitchhiker24.com"));
    assertEquals(204, response.getStatus());
  }
  
  @Test
  public void testInvalid() throws IOException, URISyntaxException
  {
    MockHttpResponse response = validate(new VObject("uid=dent,ou=People"));
    assertEquals(400, response.getStatus());
    JsonNode node = Resources.parseJson(response);
    assertEquals("is not valid", node.path("message").asText());
  }

  @Test
  public void testInvalidWithTooLongUsername() throws IOException, URISyntaxException
  {
    StringBuilder builder = new StringBuilder("a");
    for (int i=0; i<128; i++) {
      builder.append("c");
    }
    MockHttpResponse response = validate(new VObject(builder.toString()));
    assertEquals(400, response.getStatus());
    JsonNode node = Resources.parseJson(response);
    assertEquals("is not valid", node.path("message").asText());
  }
  
  
  private MockHttpResponse validate(VObject vo) throws IOException, URISyntaxException {
    Validator v = new HibernateValidator(Validation.buildDefaultValidatorFactory());
    Dispatcher dispatcher = Resources.createDispatcher(new ValidationResource(v));
    dispatcher.getProviderFactory().register(HibernateValidatorExceptionMapping.class);
    
    MockHttpRequest request = MockHttpRequest.create("POST", "/validation");
    return Resources.dispatch(dispatcher, request, vo);
  }
  
  public static class VObject {
    
    @RDN
    private String name;

    public VObject(String name)
    {
      this.name = name;
    }

    public VObject()
    {
    }

    public String getName()
    {
      return name;
    }

    public void setName(String name)
    {
      this.name = name;
    }
    
  }
  
  @Path(value = "validation")
  public static class ValidationResource {
  
    private final Validator validator;

    public ValidationResource(Validator validator)
    {
      this.validator = validator;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void validate(VObject vo){
      validator.validate(vo, "is not valid");
    }
    
  }
  
}
