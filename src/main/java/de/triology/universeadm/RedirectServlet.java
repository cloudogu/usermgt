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

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import com.google.inject.Singleton;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class RedirectServlet extends HttpServlet
{

  /** Field description */
  public static final String PARAM_PATH = "redirect.path";

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @throws ServletException
   */
  @Override
  public void init() throws ServletException
  {
    redirectPath = getServletConfig().getInitParameter(PARAM_PATH);
    
    if (Strings.isNullOrEmpty(redirectPath))
    {
      throw new ServletException("init-param for redirect.path is not defined");
    }
  }

  /**
   * Method description
   *
   *
   * @param req
   * @param resp
   *
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    resp.sendRedirect(req.getContextPath() + redirectPath);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String redirectPath;
}
