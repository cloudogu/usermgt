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

package de.triology.universeadm.settings;

import com.google.common.base.Charsets;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultCredentialsCheckerTest
{
  
  @Mock
  private HttpURLConnection connection;

  @Test
  public void testCheckCredentialsSuccess() throws IOException
  {
    DefaultCredentialsChecker checker = createCredentialsChecker();
    when(connection.getInputStream()).thenReturn(createValidInputStream());
    ByteArrayOutputStream postBody = new ByteArrayOutputStream();
    when(connection.getOutputStream()).thenReturn(postBody);
    Credentials credentials = new Credentials("trillian", "secret");
    boolean result = checker.checkCredentials(credentials, "http://hitchhikers.wikia.com/wiki/Main_Page");
    assertTrue(result);
    assertEquals("username=trillian&password=secret&action=checkCreds", postBody.toString("UTF-8"));
  }
  
  @Test
  public void testCheckCredentialsFailure() throws IOException
  {
    DefaultCredentialsChecker checker = createCredentialsChecker();
    when(connection.getInputStream()).thenReturn(createInvalidInputStream());
    ByteArrayOutputStream postBody = new ByteArrayOutputStream();
    when(connection.getOutputStream()).thenReturn(postBody);
    Credentials credentials = new Credentials("dent", "secret");
    boolean result = checker.checkCredentials(credentials, "http://hitchhikers.wikia.com/wiki/Main_Page");
    assertFalse(result);
    assertEquals("username=dent&password=secret&action=checkCreds", postBody.toString("UTF-8"));
  }
  
  private InputStream createValidInputStream(){
    return createInputStream(DefaultCredentialsChecker.RETURN_VALID);
  }
  
  private InputStream createInvalidInputStream(){
    return createInputStream(DefaultCredentialsChecker.RETURN_INVALID);
  }
  
  private InputStream createInputStream(String value)
  {
    return new ByteArrayInputStream(value.getBytes(Charsets.UTF_8));
  }
  
  private DefaultCredentialsChecker createCredentialsChecker()
  {
    return new DefaultCredentialsChecker(){

      @Override
      protected HttpURLConnection createUrlConnection(URL url) throws IOException
      {
        return connection;
      }
      
    };
  }
  
}
