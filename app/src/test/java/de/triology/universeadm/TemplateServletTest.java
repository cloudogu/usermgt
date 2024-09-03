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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateServletTest
{
 
  @Mock
  private HttpServletRequest request;
  
  @Mock
  private HttpServletResponse response;

  
  @Test
  public void testDoGet() throws ServletException, IOException
  {
    TemplateServlet servlet = createTemplateServlet(res("de/triology/universeadm/index.001.txt"));
    when(request.getContextPath()).thenReturn("/universeadm/");
    when(request.getRequestURI()).thenReturn("/universeadm/");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (PrintWriter writer = new PrintWriter(baos))
    {
      when(response.getWriter()).thenReturn(writer);
      servlet.doGet(request, response);
      writer.flush();
    }
    assertEquals("ctx: /universeadm", baos.toString().trim());
  }
  
  @Test
  public void testNotFound() throws ServletException, IOException
  {
    TemplateServlet servlet = createTemplateServlet(null);
    when(request.getContextPath()).thenReturn("/universeadm/");
    when(request.getRequestURI()).thenReturn("/universeadm/");
    servlet.doGet(request, response);
    verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
  }
  
  private URL res(String path){
    return com.google.common.io.Resources.getResource(path);
  }
  
  private TemplateServlet createTemplateServlet(URL url) throws MalformedURLException
  {
    final ServletContext ctx = mock(ServletContext.class);
    when(ctx.getResource("/index.html")).thenReturn(url);
    return new TemplateServlet(){

      @Override
      public ServletContext getServletContext()
      {
        return ctx;
      }
    };
  }
  
}
