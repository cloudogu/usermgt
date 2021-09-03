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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;

import org.apache.shiro.cas.CasAuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

/**
 * Client for the cas rest api.
 * 
 * @see <a href="https://wiki.jasig.org/display/casum/restful+api">CAS RESTful API</a>
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class CasRestClient
{

  /** Field description */
  private static final String CAS_V1_TICKETS = "/v1/tickets";

  /** Field description */
  private static final String ENCODING = "UTF-8";

  /** Field description */
  private static final String HEADER_LOCATION = "Location";

  /** the logger for CasRestClient. */
  private static final Logger logger =
    LoggerFactory.getLogger(CasRestClient.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param casServerUrl
   * @param serviceUrl
   */
  public CasRestClient(String casServerUrl, String serviceUrl)
  {
    this.casServerUrl = casServerUrl;
    this.serviceUrl = serviceUrl;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Creates a service ticket for the given username and password.
   *
   * @param username username
   * @param password password
   * 
   * @return service ticket
   */
  public String createServiceTicket(final String username, final String password)
  {
    String st = null;

    try
    {
      String tgt = createGrantingTicket(casServerUrl, username, password);

      logger.debug("TGT is : {}", tgt);

      st = createServiceTicket(tgt);

      logger.debug("ST is : {}", st);
    }
    catch (IOException ex)
    {
      throw new CasAuthenticationException("cas validation failed", ex);
    }

    return st;
  }

  /**
   * Method description
   *
   *
   * @param connection
   * @param username
   * @param password
   *
   * @throws IOException
   */
  private void appendCredentials(HttpURLConnection connection, String username,
    String password)
    throws IOException
  {
    StringBuilder buffer = new StringBuilder();

    buffer.append("username=").append(encode(username));
    buffer.append("&password=").append(encode(password));

    try (BufferedWriter bwr = createWriter(connection))
    {

      bwr.write(buffer.toString());
      bwr.flush();
    }
  }

  /**
   * Method description
   *
   *
   * @param connection
   * @param serviceUrl
   *
   * @throws IOException
   */
  private void appendServiceUrl(HttpURLConnection connection) throws IOException
  {
    String encodedServiceURL = "service=".concat(encode(serviceUrl));

    logger.debug("Service url is : {}", encodedServiceURL);

    try (BufferedWriter writer = createWriter(connection))
    {
      writer.write(encodedServiceURL);
      writer.flush();
    }
  }

  /**
   * Method description
   *
   *
   * @param c
   */
  private void close(HttpURLConnection c)
  {
    if (c != null)
    {
      c.disconnect();
    }
  }

  /**
   * Method description
   *
   *
   * @param casServerUrl
   * @param username
   * @param password
   *
   * @return
   *
   * @throws IOException
   */
  private String createGrantingTicket(String casServerUrl, String username,
    String password)
    throws IOException
  {
    HttpURLConnection connection = null;

    try
    {
      connection = open(casServerUrl + CAS_V1_TICKETS);
      appendCredentials(connection, username, password);

      int rc = connection.getResponseCode();

      if (rc != HttpServletResponse.SC_CREATED)
      {
        throw new CasAuthenticationException(
          "could not create granting ticket, web service returned " + rc);
      }

      String location = connection.getHeaderField(HEADER_LOCATION);

      if (Strings.isNullOrEmpty(location))
      {
        throw new CasAuthenticationException(
          "could not create granting ticket, web service returned no location header");
      }

      return extractTgtFromLocation(location);
    }
    finally
    {
      close(connection);
    }
  }

  /**
   * Method description
   *
   *
   * @param connection
   *
   * @return
   *
   * @throws IOException
   */
  private BufferedReader createReader(HttpURLConnection connection)
    throws IOException
  {
    return new BufferedReader(
      new InputStreamReader(connection.getInputStream(), Charsets.UTF_8));
  }

  /**
   * Method description
   *
   *
   * @param serverUrl
   * @param serviceUrl
   * @param tgt
   *
   * @return
   *
   * @throws IOException
   */
  private String createServiceTicket(String tgt) throws IOException
  {
    String st = null;
    HttpURLConnection connection = null;

    try
    {
      connection = open(createServiceTicketUrl(tgt));
      appendServiceUrl(connection);

      int rc = connection.getResponseCode();

      if (rc != HttpServletResponse.SC_OK)
      {
        throw new CasAuthenticationException(
          "could not create service ticket, web service returned " + rc);
      }

      String content;

      try (BufferedReader reader = createReader(connection))
      {
        content = CharStreams.toString(reader);
      }

      if (Strings.isNullOrEmpty(content))
      {
        throw new CasAuthenticationException(
          "could not create service ticket, body is empty");
      }

      st = content.trim();

    }
    finally
    {
      close(connection);
    }

    return st;
  }

  /**
   * Method description
   *
   *
   * @param serverUrl
   * @param tgt
   *
   * @return
   */
  private String createServiceTicketUrl(String tgt)
  {
    return casServerUrl + CAS_V1_TICKETS + "/" + tgt;
  }

  /**
   * Method description
   *
   *
   * @param connection
   *
   * @return
   *
   * @throws IOException
   */
  private BufferedWriter createWriter(HttpURLConnection connection)
    throws IOException
  {
    return new BufferedWriter(
      new OutputStreamWriter(connection.getOutputStream(), Charsets.UTF_8));
  }

  /**
   * Method description
   *
   *
   * @param value
   *
   * @return
   *
   * @throws UnsupportedEncodingException
   */
  private String encode(String value)
  {
    try
    {
      return URLEncoder.encode(value, ENCODING);
    }
    catch (UnsupportedEncodingException ex)
    {
      throw new CasAuthenticationException("failure durring encoding", ex);
    }
  }

  /**
   * Method description
   *
   *
   * @param location
   *
   * @return
   */
  private String extractTgtFromLocation(String location)
  {
    int index = location.lastIndexOf('/');

    if (index < 0)
    {
      throw new CasAuthenticationException(
        "could not create granting ticket, web service returned invalid location header");
    }

    return location.substring(index+1);
  }

  /**
   * Open connection to CAS.
   *
   * @param url
   * @return URL Connection
   * @throws MalformedURLException
   * @throws IOException
   */
  private HttpURLConnection open(final String url) throws IOException
  {
    HttpURLConnection connection =
      (HttpURLConnection) new URL(url).openConnection();

    connection.setRequestMethod("POST");
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    return connection;

  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final String casServerUrl;

  /** Field description */
  private final String serviceUrl;
}
