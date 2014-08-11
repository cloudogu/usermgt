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

import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import de.triology.universeadm.AdamsQuote;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.shiro.authz.AuthorizationException;
import org.joda.time.DateTime;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
  configuration = "classpath:de/triology/universeadm/shiro.001.ini", 
  username = "trillian", 
  password = "secret"
)
public class BackupManagerTest
{

  private BackupManager backupManager;
  private File directory;
  
  @Before
  public void initBackupManager() throws IOException{
    directory = tempFolder.newFolder();
    backupManager = new DefaultBackupManager(new BackupConfiguration(directory));
  }
  
  private void addFiles(BackupFile... files) throws IOException
  {
    for (BackupFile file : files)
    {
      addFile(file);
    }
  }
  
  private void addFile(BackupFile file) throws IOException
  {
    addFile(file.getName(), file.getLastModified(), (int) file.getSize());
  }

  private void addFile(String name, DateTime lastModified, int size) throws IOException {
    addFile(name, lastModified, new byte[size]);
  }
  
  private void addFile(String name, DateTime lastModified, byte[] content) throws IOException {
    File file = new File(directory, name);
    Files.write(content, file);
    if (!file.setLastModified(lastModified.getMillis()))
    {
      throw new IllegalStateException("could not change last modified time");
    }
  }
  
  private DateTime now(){
    return DateTime.now().withMillisOfSecond(0);
  }
  
  @Test
  public void testGetFiles() throws IOException
  {
    BackupFile a = new BackupFile("a.txt", now().minusHours(2).withMillisOfSecond(0), 256);
    BackupFile b = new BackupFile("b.txt", now().minusDays(2).withMillisOfSecond(0), 1024);
    BackupFile c = new BackupFile("c.txt", now().plusDays(2).withMillisOfSecond(0), 2056);
    addFiles(a, b, c);
    List<BackupFile> files = backupManager.getBackupFiles();
    assertThat(files.size(), is(3));
    assertThat(files, contains(a, b, c));
  }
  
  @Test(expected = AuthorizationException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testGetFilesUnprivileged()
  {
    backupManager.getBackupFiles();
  }
  
  @Test
  public void testGetFile() throws IOException
  {
    BackupFile a = new BackupFile("a.txt", now().minusHours(2).withMillisOfSecond(0), 256);
    BackupFile b = new BackupFile("b.txt", now().minusDays(2).withMillisOfSecond(0), 1024);
    addFiles(a, b);
    BackupFile file = backupManager.get("a.txt");
    assertThat(file, equalTo(a));
    file = backupManager.get("c.txt");
    assertThat(file, nullValue());
  }
  
  @Test(expected = BackupFileIllegalNameException.class)
  public void testGetFileIllegalName() throws IOException
  {
    backupManager.get("c..txt");
  }
  
  @Test(expected = AuthorizationException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testGetFileUnprivileged()
  {
    backupManager.get("a.txt");
  }
  
  @Test
  public void testGetContent() throws IOException
  {
    DateTime time = now();
    String assertContent = AdamsQuote.ENDED_UP;
    addFile("adams.txt", time, assertContent.getBytes(Charsets.UTF_8));
    BackupFile file = backupManager.get("adams.txt");
    try (InputStream c = backupManager.getContent(file))
    {
      String content = new String(ByteStreams.toByteArray(c), Charsets.UTF_8);
      assertThat(content, equalTo(assertContent));
    }
  }
  
  @Test(expected = BackupFileDoesNotExistsException.class)
  public void testGetContentDoesNotExists() throws IOException
  {
    backupManager.getContent(new BackupFile("a", now(), 16));
  }
  
  @Test(expected = BackupFileIllegalNameException.class)
  public void testGetContentIllegalName() throws IOException
  {
    backupManager.getContent(new BackupFile("test/test", now(), 16));
  }
  
  @Test(expected = BackupFileIllegalNameException.class)
  public void testGetContentIllegalName2() throws IOException
  {
    backupManager.getContent(new BackupFile("..", now(), 16));
  }
  
  @Test(expected = BackupFileIllegalNameException.class)
  public void testGetContentIllegalName3() throws IOException
  {
    backupManager.getContent(new BackupFile("\\test", now(), 16));
  }
  
  @Test(expected = AuthorizationException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testGetContentUnprivileged() throws IOException
  {
    backupManager.get("adams.txt");
  }
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
  
  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
}
