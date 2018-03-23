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



package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import de.triology.universeadm.RestError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Map {@link UserSelfRemoveException} to http status code 409 (Conflict).
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @since 1.1.0
 */
@Provider
public class UserSelfRemoveExceptionMapper
  implements ExceptionMapper<UserSelfRemoveException>
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(UserSelfRemoveExceptionMapper.class);

  //~--- methods --------------------------------------------------------------

  /**
   * Return status code 409.
   *
   *
   * @param exception user self remove exception
   *
   * @return response with status code 409
   */
  @Override
  public Response toResponse(UserSelfRemoveException exception)
  {
    StringBuilder buffer = new StringBuilder("the user ");

    buffer.append(exception.getPrincipal());
    buffer.append(" has tried to remove himself");

    String message = buffer.toString();

    logger.warn(message);

    return Response.status(Response.Status.CONFLICT).type(
      MediaType.APPLICATION_JSON_TYPE).entity(new RestError(message)).build();
  }
}
