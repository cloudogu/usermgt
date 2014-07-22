/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm.validation;

import com.google.common.collect.Lists;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Provider
public class HibernateValidatorExceptionMapping implements ExceptionMapper<ConstraintViolationException>
{

  private static final Logger logger = LoggerFactory.getLogger(HibernateValidatorExceptionMapping.class);
  
  @Override
  public Response toResponse(ConstraintViolationException exception)
  {
    logger.warn("map validation exception", exception);
    
    List<ConstraintViolationBean> violations = Lists.newArrayList();
    for ( ConstraintViolation<?> violation : exception.getConstraintViolations() )
    {
      violations.add(new ConstraintViolationBean(violation));
    }
    
    return Response
      .status(Response.Status.BAD_REQUEST)
      .type(MediaType.APPLICATION_JSON_TYPE)
      .entity(new ValidationFailure(exception.getMessage(), violations))
      .build();
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "validation-failure")
  public static class ValidationFailure {
    
    private String message;
    @XmlElement(name = "violation")
    @XmlElementWrapper(name = "violations")
    private List<ConstraintViolationBean> violoations;

    ValidationFailure(){}
    
    public ValidationFailure(String message, List<ConstraintViolationBean> violoations)
    {
      this.message = message;
      this.violoations = violoations;
    }

    public String getMessage()
    {
      return message;
    }

    public List<ConstraintViolationBean> getVioloations()
    {
      return violoations;
    }
    
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "violation")
  public static class ConstraintViolationBean 
  {
    
    private String path;
    private String message;

    ConstraintViolationBean(){}

    public ConstraintViolationBean(ConstraintViolation<?> violation)
    {
      message = violation.getMessage();
      path = violation.getPropertyPath().toString();
    }

    public String getMessage()
    {
      return message;
    }

    public String getPath()
    {
      return path;
    }
    
  }
  
}
