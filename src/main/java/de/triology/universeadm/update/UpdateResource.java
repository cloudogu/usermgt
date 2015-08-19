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
package de.triology.universeadm.update;

import static com.google.common.io.Files.touch;
import com.google.inject.Inject;
import static de.triology.universeadm.update.JsonUtils.loadJsonFile;
import static de.triology.universeadm.update.UpdateConstants.DATE_TIME_FORMAT;
import static de.triology.universeadm.update.UpdateConstants.SCM_ACTUAL_VERSION_FILE;
import static de.triology.universeadm.update.UpdateConstants.SCM_UPDATE_AVAILABLE_FLAG;
import static de.triology.universeadm.update.UpdateConstants.SCM_UPDATE_FORM_DATA;
import static de.triology.universeadm.update.UpdateConstants.SCM_UPDATE_FORM_DATA_OUTPUT_FILE;
import static de.triology.universeadm.update.UpdateConstants.SCM_UPDATE_PRECHECK_RESULT_FILE;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mbehlendorf
 */
@Path("update")
public class UpdateResource {

  private static final Logger logger = LoggerFactory.getLogger(UpdateResource.class);
  private final UpdateService updateService;

  public UpdateResource() {
    this.updateService = null;
  }

  @Inject
  public UpdateResource(UpdateService updateService) {
    this.updateService = updateService;
  }

  @POST
  @Path("start")
  public void start() {
    updateService.startUpdate();
  }

  @POST
  @Path("check")
  @Produces(MediaType.APPLICATION_JSON)
  public ResultCheck check() throws IOException, ParseException {

    Status status = updateService.checkUpdate();
    ResultCheck result = new ResultCheck();

    if (status.scmUpdateInProgress) {
      if (status.scmUpdatePreCheckRunning) {
        result.setSuccess(true);
        result.setStatus("preCheck");
      }
      else if (status.scmUpdatePreCheckDone) {
        if (UpdateConstants.SCM_UPDATE_PRECHECK_RESULT_FILE.exists()) {

          result.setSuccess(true);
          result.setStatus("preCheckResult");
          result.setSyscheck(loadJsonFile(SCM_UPDATE_PRECHECK_RESULT_FILE));

        }
      }
      else if (status.scmUpdateFormAvailable) {
          result.setSuccess(true);
          result.setStatus("updateFormAvailable");
      }
      else {
        result.setSuccess(true);
        result.setStatus("in progress");
      }
    }
    else if (!status.scmUpdateInProgress && status.updateScheduled) {
      result.setSuccess(true);
      result.setStatus("scheduled");
    }
    else if (!status.scmUpdateInProgress) {
      if (status.updateSuccess) {
        result.setSuccess(true);
        result.setStatus("done");
        result.setResult("successful");
        result.setDatetime(new SimpleDateFormat(DATE_TIME_FORMAT));
      }
      else if (status.updateFailed) {
        if (status.wrongUserOrPassword) {
          result.setSuccess(true);
          result.setStatus("wrong credentials");
          result.setDatetime(new SimpleDateFormat(DATE_TIME_FORMAT));
        }
        else {
          result.setSuccess(true);
          result.setStatus("done");
          result.setResult("failed");
          result.setDatetime(new SimpleDateFormat(DATE_TIME_FORMAT));
        }
      }
      else {
        result.setSuccess(true);
        result.setStatus("done");
        result.setResult("unknown");
      }
    }
    if (status.noUpdateForVersion || status.noFileForVersion) {
      result.setSuccess(true);
      result.setStatus("no update");
    }
    return result;
  }

  @POST
  @Path("updateCheck")
  @Produces(MediaType.APPLICATION_JSON)
  public ResultUpdateCheck updateCheck() {
    Status status = updateService.checkUpdate();
    ResultUpdateCheck result = new ResultUpdateCheck();
    // update check status
    result.setValidCreds(status.validCreds);
    if (status.updateAvailable) {
      result.setUpdateAvailable(true);
    }
    if (status.updateCheckToggleBusy) {
      result.setSuccess(true);
      result.setStatus("waiting");
    }
    else if (status.checkForUpdates) {
      result.setSuccess(true);
      result.setStatus("scheduled");
      result.setUpdateAvailable(status.updateAvailable);
    }
    else if (!status.checkForUpdates && !status.updateCheckToggleBusy) {
      result.setSuccess(true);
      result.setStatus("deactivated");
    }
    return result;
  }

  @GET
  @Path("versionCheck")
  @Produces(MediaType.APPLICATION_JSON)
  public ResultVersionCheck versionCheck() {
    ResultVersionCheck result = new ResultVersionCheck();
    result.setVersion("unknown version");
    
      FileReader fr;
      try {
        if (SCM_UPDATE_AVAILABLE_FLAG.exists()) {
          fr = new FileReader(SCM_UPDATE_AVAILABLE_FLAG);
          BufferedReader br = new BufferedReader(fr);
          result.setNewVersion(br.readLine());
        }
        
        if (SCM_ACTUAL_VERSION_FILE.exists()) {
          fr = new FileReader(SCM_ACTUAL_VERSION_FILE);
          BufferedReader br = new BufferedReader(fr);
          result.setVersion(br.readLine());
        }
        
      }
      catch (FileNotFoundException ex) {
        logger.error("Can not read File: ", SCM_UPDATE_AVAILABLE_FLAG);

      }
      catch (IOException ex) {
        logger.error("Can not read Line in File: ", SCM_UPDATE_AVAILABLE_FLAG);
      }

    
    return result;
  }

  @GET
  @Path("preCheckResult")
  @Produces(MediaType.TEXT_PLAIN)
  public String preCheckResult() {
    String result = "";
    if (SCM_UPDATE_PRECHECK_RESULT_FILE.exists()) {
      try {
        result = FileUtils.readFileToString(SCM_UPDATE_PRECHECK_RESULT_FILE);
      }
      catch (IOException ex) {
        logger.error("Can not read File: ", SCM_UPDATE_PRECHECK_RESULT_FILE);
      }
    }
    return result;
  }

  @GET
  @Path("userInput")
  @Produces(MediaType.TEXT_PLAIN)
  public String userInput() {
    String result = "";
    if (SCM_UPDATE_FORM_DATA.exists()) {
      try {
        result = FileUtils.readFileToString(SCM_UPDATE_FORM_DATA);
      }
      catch (IOException ex) {
        logger.error("Can not read File: ", SCM_UPDATE_FORM_DATA);
      }
    }
    return result;
  }
  
  @POST
  @Path("sendUserInput")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Result sendUserInput(String input) {
    if (!SCM_UPDATE_FORM_DATA_OUTPUT_FILE.exists()) {
      try {
        touch(SCM_UPDATE_FORM_DATA_OUTPUT_FILE);
      }
      catch (IOException ex) {
        logger.error("Can not touch File: ", SCM_UPDATE_FORM_DATA_OUTPUT_FILE);
      }
    }
    try {
      FileUtils.writeStringToFile(SCM_UPDATE_FORM_DATA_OUTPUT_FILE,input);
      return Result.getSuccessMessage();
    }
    catch (IOException ex) {
      logger.error("Unable to write in: ",SCM_UPDATE_FORM_DATA_OUTPUT_FILE);
    }
    return Result.getFailMessage();
  }

  @POST
  @Path("preCheckAction")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Result preCheckAction(String action) {
    ResultPreCheckAction result = new ResultPreCheckAction();
    result.setResult("");
    switch (action) {
      case "ignore":
      case "ok":
        result.setResult("ok");
        break;
      case "abort":
        result.setResult("abort");
        break;
      case "recheck":
        result.setResult("recheck");
        break;
    }
    if (!result.getResult().isEmpty()) {
      try {
        //updateService.touchFlag(UpdateConstants.SCM_UPDATE_PRECHECK_DONE_FLAG);
        FileUtils.writeStringToFile(UpdateConstants.SCM_UPDATE_PRECHECK_DONE_FLAG, result.getResult());
        return Result.getSuccessMessage();
      }
      catch (IOException ex) {
        logger.error("Unable to write in: ", UpdateConstants.SCM_UPDATE_PRECHECK_DONE_FLAG);
      }

    }

    return Result.getFailMessage();
  }

}
