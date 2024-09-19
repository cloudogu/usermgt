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
