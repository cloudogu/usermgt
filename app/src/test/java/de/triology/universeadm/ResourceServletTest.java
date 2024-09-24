package de.triology.universeadm;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class ResourceServletTest
{
  
  public ResourceServletTest()
  {
  }

  @Test
  public void testIsPathIllegal()
  {
    assertTrue(ResourceServlet.isPathIllegal("/WEB-INF/web.xml"));
    assertTrue(ResourceServlet.isPathIllegal("WEB-INF/web.xml"));
    assertTrue(ResourceServlet.isPathIllegal("web-inf/web.xml"));
    assertTrue(ResourceServlet.isPathIllegal("/WEB-INF/other"));
    assertTrue(ResourceServlet.isPathIllegal("/WEB-INF"));
    assertTrue(ResourceServlet.isPathIllegal("WEB-INF"));
    assertTrue(ResourceServlet.isPathIllegal("META-INF"));
    assertTrue(ResourceServlet.isPathIllegal("/META-INF"));
    assertTrue(ResourceServlet.isPathIllegal("/META-INF/MANIFEST.MF"));
    assertFalse(ResourceServlet.isPathIllegal("/index.html"));
    assertFalse(ResourceServlet.isPathIllegal("/style/some.css"));
  }
  
}
