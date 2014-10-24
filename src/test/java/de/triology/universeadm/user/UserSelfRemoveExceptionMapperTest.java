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
public class UserSelfRemoveExceptionMapperTest
{

  /**
   *   Method description
   *  
   *  
   *   @throws IOException
   *   @throws URISyntaxException
   */
  @Test
  public void testMapping() throws URISyntaxException, IOException
  {
    MockHttpResponse response = dispatch("/a/ex");

    assertEquals(HttpServletResponse.SC_CONFLICT, response.getStatus());
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

    dispatcher.getProviderFactory().register(
      UserSelfRemoveExceptionMapper.class);

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
      throw new UserSelfRemoveException("my message", "my principal");
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
