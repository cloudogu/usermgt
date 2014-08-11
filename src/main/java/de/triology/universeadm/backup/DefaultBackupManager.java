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

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.Roles;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultBackupManager implements BackupManager
{
  
  private static final String CONFIGURATION = "backup.xml";
  
  private static final String[] ILLEGAL_STRINGS = {"/", "..", "\\"};
  
  
  private final BackupConfiguration configuration;

  public DefaultBackupManager()
  {
    this(BaseDirectory.getConfiguration(CONFIGURATION, BackupConfiguration.class));
  }  
  
  DefaultBackupManager(BackupConfiguration configuration)
  {
    this.configuration = configuration;
  }

  @Override
  public List<BackupFile> getBackupFiles()
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
   
    List<BackupFile> files = Lists.newArrayList();
    File directory = configuration.getDirectory();
    for ( File file : directory.listFiles(new OnlyFilesFilter()) )
    {
      files.add(convert(file));
    }
    return Ordering.natural().immutableSortedCopy(files);
  }
  
  private BackupFile convert(File file){
    return new BackupFile(file.getName(), new DateTime(file.lastModified()), file.length());
  }

  @Override
  public BackupFile get(String name)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    
    if (isNameInvalid(name))
    {
      throw new BackupFileIllegalNameException("file name is illegal");
    }
    
    BackupFile bf = null;
    File file = new File(configuration.getDirectory(), name);
    if (file.exists())
    {
      bf = convert(file);
    }
    return bf;
  }
  
  @Override
  public InputStream getContent(BackupFile file) throws FileNotFoundException
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    
    if (isNameInvalid(file.getName()))
    {
      throw new BackupFileIllegalNameException("file name is illegal");
    }
    
    InputStream input = null;
    File f = new File(configuration.getDirectory(), file.getName());
    if (f.exists())
    {
      input = new BufferedInputStream(new FileInputStream(f));
    } 
    else 
    {
      throw new BackupFileDoesNotExistsException(String.format("file %s does not exists", file.getName()));
    }
    return input;
  }
  
  private boolean isNameInvalid(String name){
    boolean invalid = false;
    for ( String is : ILLEGAL_STRINGS )
    {
      if (name.contains(is))
      {
        invalid = true;
        break;
      }
    }
    return invalid;
  }
  
  private static class OnlyFilesFilter implements FileFilter {

    @Override
    public boolean accept(File file)
    {
      return file.isFile();
    }
  
  }
  
}
