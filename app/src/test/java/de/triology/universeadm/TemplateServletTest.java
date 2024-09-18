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
