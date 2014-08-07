/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ssdorra
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
