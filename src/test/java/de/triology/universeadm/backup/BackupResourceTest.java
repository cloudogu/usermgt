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

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import de.triology.universeadm.AdamsQuote;
import de.triology.universeadm.Resources;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(MockitoJUnitRunner.class)
public class BackupResourceTest
{

  @Mock
  private BackupManager backupManager;
  
  @Test
  public void testGetAll() throws URISyntaxException, IOException
  {
    DateTime now = DateTime.now();
    List<BackupFile> files = Lists.newArrayList(new BackupFile("a", now, 16l));
    when(backupManager.getBackupFiles()).thenReturn(files);
    MockHttpResponse response = dispatch("/backup");
    JsonNode node = Resources.parseJson(response);
    assertTrue(node.isArray());
    JsonNode fileNode = node.get(0);
    assertThat(fileNode, notNullValue());
    assertThat(fileNode.path("name").asText(), is("a"));
    assertThat(new DateTime(fileNode.path("lastModified").asText()), is(now));
    assertThat(fileNode.path("size").asLong(), is(16l));
  }
  
  @Test
  public void testGet() throws URISyntaxException, IOException
  {
    DateTime now = DateTime.now();
    when(backupManager.get("a")).thenReturn(new BackupFile("a", now, 16l));
    MockHttpResponse response = dispatch("/backup/a");
    assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    JsonNode fileNode = Resources.parseJson(response);
    assertThat(fileNode.path("name").asText(), is("a"));
    assertThat(new DateTime(fileNode.path("lastModified").asText()), is(now));
    assertThat(fileNode.path("size").asLong(), is(16l));
  }
  
  @Test
  public void testGetNotFound() throws URISyntaxException, IOException
  {
    MockHttpResponse response = dispatch("/backup/test");
    assertThat(response.getStatus(), is(HttpServletResponse.SC_NOT_FOUND));
  }
  
  @Test
  public void testGetContent() throws IOException, URISyntaxException
  {
    String content = AdamsQuote.GRAPEFRUITE_LIFE;
    byte[] contentAsBytes = content.getBytes(Charsets.UTF_8);
    BackupFile file = new BackupFile("a", DateTime.now(), contentAsBytes.length);
    when(backupManager.get("a")).thenReturn(file);
    when(backupManager.getContent(file)).thenReturn(new ByteArrayInputStream(contentAsBytes));
    MockHttpResponse response = dispatch("/backup/a/content");
    assertThat(response.getStatus(), is(HttpServletResponse.SC_OK));
    assertThat(response.getContentAsString(), is(content));
  }
  
  @Test
  public void testGetContentNotFound() throws URISyntaxException, IOException
  {
    MockHttpResponse response = dispatch("/backup/test/content");
    assertThat(response.getStatus(), is(HttpServletResponse.SC_NOT_FOUND));
  }
  
  private MockHttpResponse dispatch(String path) throws URISyntaxException, IOException
  {
    return Resources.dispatch(new BackupResource(backupManager), MockHttpRequest.get(path));
  }
  
}
