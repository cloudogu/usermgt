/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Provider
public class CredentialsInvalidSettingsExceptionMapper implements ExceptionMapper<CredentialsInvalidSettingsException>
{

  private static final Logger logger = LoggerFactory.getLogger(CredentialsInvalidSettingsExceptionMapper.class);
  
  @Override
  public Response toResponse(CredentialsInvalidSettingsException exception)
  {
    logger.warn("invalid credentials entered", exception);
    
    return Response.status(Response.Status.BAD_REQUEST).build();
  }
  
}
