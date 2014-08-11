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

package de.triology.universeadm.backup;

import de.triology.universeadm.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class BackupFileIllegalNameExceptionMapperTest
{
  
  @Test
  public void testMappingWithoutError() throws URISyntaxException, IOException
  {
    MockHttpResponse res = dispatch("/test");
    assertThat(res.getStatus(), is(HttpServletResponse.SC_NO_CONTENT));
  }
  
  @Test
  public void testMapping() throws URISyntaxException, IOException
  {
    MockHttpResponse res = dispatch("/test/ex");
    assertThat(res.getStatus(), is(HttpServletResponse.SC_BAD_REQUEST));
  }
  
  private MockHttpResponse dispatch(String path) throws URISyntaxException, IOException
  {
    return Resources.dispatchWithProviders(new Resource(), MockHttpRequest.get(path), BackupFileIllegalNameExceptionMapper.class);
  }
  
  
  @Path("test")
  public static class Resource {
    
    @GET
    public void normal(){
      
    }
    
    @GET
    @Path("/ex")
    public void exception(){
      throw new BackupFileIllegalNameException("illegal");
    }
    
  }
  
}
