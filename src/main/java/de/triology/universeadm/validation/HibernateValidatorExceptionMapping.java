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
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
