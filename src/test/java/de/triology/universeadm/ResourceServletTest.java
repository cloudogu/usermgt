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
