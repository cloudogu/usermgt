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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("backup")
public class BackupResource
{
  
  @VisibleForTesting
  static final String HEADER_LENGTH = "Content-Length";
  
  @VisibleForTesting
  static final String HEADER_DISPOSITION = "Content-Disposition";
  
  static final String HEADER_DISPOSITION_TPL = "attachment; filename=\"%s\"";
  
  private final BackupManager backupManager;

  @Inject
  public BackupResource(BackupManager backupManager)
  {
    this.backupManager = backupManager;
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public List<BackupFile> getFiles(){
    return backupManager.getBackupFiles();
  }
  
  @GET
  @Path("{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFile(@PathParam("name") String name){
    Response.ResponseBuilder builder;
    BackupFile file = backupManager.get(name);
    if ( file != null ){
      builder = Response.ok(file);
    } else {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    return builder.build();
  }
  
  @GET
  @Path("{name}/content")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFileContent(@PathParam("name") String name) throws IOException{
    Response.ResponseBuilder builder;
    BackupFile file = backupManager.get(name);
    if ( file != null ){
      final InputStream input = backupManager.getContent(file);
      StreamingOutput output = new StreamingOutput()
      {

        @Override
        public void write(OutputStream output) throws IOException, WebApplicationException
        {
          try 
          {
            ByteStreams.copy(input, output);
            output.flush();
          } finally {
            input.close();
          }
        }
      };
      builder = Response.ok(output)
                        .lastModified(file.getLastModified().toDate())
                        .header(HEADER_LENGTH, file.getSize())
                        .header(HEADER_DISPOSITION, String.format(HEADER_DISPOSITION_TPL, file.getName()))
                        .type(MediaType.APPLICATION_OCTET_STREAM_TYPE);
    } else {
      builder = Response.status(Response.Status.NOT_FOUND);
    }
    return builder.build();
  }
}
