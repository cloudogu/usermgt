/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author ssdorra
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
    TemplateServlet servlet = createTemplateServlet();
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
  
  private TemplateServlet createTemplateServlet() throws MalformedURLException
  {
    URL url = com.google.common.io.Resources.getResource("de/triology/universeadm/index.001.txt");
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
