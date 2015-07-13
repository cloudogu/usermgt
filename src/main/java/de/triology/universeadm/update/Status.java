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

import static de.triology.universeadm.update.UpdateConstants.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mbehlendorf
 */
@XmlRootElement(name = "Status")
@XmlAccessorType(XmlAccessType.FIELD)
public class Status {
  private static final Logger logger = LoggerFactory.getLogger(Status.class);

    public boolean scmUpdateInProgress;
    public boolean updateScheduled;
    public boolean wrongUserOrPassword;
    public boolean noUpdateForVersion;
    public boolean updateAvailable;
    public boolean noFileForVersion;
    public boolean checkForUpdates;
    public boolean updateSuccess;
    public boolean updateFailed;
    public boolean updateCheckToggleBusy;
    public boolean scmUpdatePreCheckRunning;
    public boolean scmUpdatePreCheckDone;
    public boolean scmUpdatePreCheckUserDecisionNeeded;
    public boolean scmUpdateFormAvailable;
    public boolean validCreds;
    
    public Status(){
        this.updateStatusVars();
    }
    
    public Status(boolean scmUpdateInProgress,boolean updateScheduled,boolean wrongUserOrPassword,
            boolean noUpdateForVersion,boolean updateAvailable,boolean noFileForVersion,boolean checkForUpdates,
            boolean updateSuccess,boolean updateFailed,boolean updateCheckToggleBusy,boolean scmUpdatePreCheckRunning,
            boolean scmUpdatePreCheckDone,boolean scmUpdatePreCheckUserDecisionNeeded,boolean scmUpdateFormAvailable,
            boolean validCreds){
      this.scmUpdateInProgress=scmUpdateInProgress;
      this.updateScheduled=updateScheduled;
      this.wrongUserOrPassword=wrongUserOrPassword;
      this.noUpdateForVersion=noUpdateForVersion;
      this.updateAvailable=updateAvailable;
      this.noFileForVersion=noFileForVersion;
      this.checkForUpdates=checkForUpdates;
      this.updateSuccess=updateSuccess;
      this.updateFailed=updateFailed;
      this.updateCheckToggleBusy=updateCheckToggleBusy;
      this.scmUpdatePreCheckRunning=scmUpdatePreCheckRunning;
      this.scmUpdatePreCheckDone=scmUpdatePreCheckDone;
      this.scmUpdatePreCheckUserDecisionNeeded=scmUpdatePreCheckUserDecisionNeeded;
      this.scmUpdateFormAvailable=scmUpdateFormAvailable;
      this.validCreds=validCreds;
    }

    private void updateStatusVars() {
        scmUpdateInProgress = SCM_UPDATE_IN_PROGRESS_FLAG.exists();
        updateScheduled = SCM_UPDATE_EXECUTOR_FLAG.exists() && SCM_UPDATE_AVAILABLE_FLAG.exists();
        wrongUserOrPassword = SCM_UPDATE_WRONG_CREDENTIALS_FLAG.exists();
        noUpdateForVersion = SCM_UPDATE_NO_UPDATE_FOR_VERSION_FLAG.exists() && ( !SCM_UPDATE_SUCCESS_FLAG.exists() || SCM_UPDATE_FAIL_FLAG.exists());
        updateAvailable = SCM_UPDATE_AVAILABLE_FLAG.exists();
        noFileForVersion = SCM_UPDATE_NO_FILE_FOR_VERSION_FLAG.exists();
        checkForUpdates = !SCM_UPDATE_DISABLE_CHECK_FOR_UPDATES_FLAG.exists() && SCM_UPDATE_CRONJOB.exists() || SCM_UPDATE_ACTIVATE_CHECK_FOR_UPDATES_FLAG.exists();
        updateSuccess = SCM_UPDATE_SUCCESS_FLAG.exists();
        updateFailed = SCM_UPDATE_FAIL_FLAG.exists();
        updateCheckToggleBusy = SCM_UPDATE_ACTIVATE_CHECK_FOR_UPDATES_FLAG.exists() || SCM_UPDATE_DISABLE_CHECK_FOR_UPDATES_FLAG.exists();
        scmUpdatePreCheckRunning = SCM_UPDATE_PRECHECK_RUNNING_FLAG.exists();
        scmUpdatePreCheckDone = SCM_UPDATE_PRECHECK_DONE_FLAG.exists();
        scmUpdatePreCheckUserDecisionNeeded = SCM_UPDATE_PRECHECK_USER_DECISION_NEEDED_FLAG.exists();
        scmUpdateFormAvailable = SCM_UPDATE_FORM_AVAILABLE_FLAG.exists() && !SCM_UPDATE_FORM_DATA_OUTPUT_FILE.exists();
        validCredsStatus();
        
    }
    private void validCredsStatus(){
      FileReader fr;
      validCreds=false;
      try {
        fr = new FileReader(SCM_UPDATE_CREDENTIALS_FILE);
        BufferedReader br = new BufferedReader(fr);
        String lastLine="";
        String line;
        while((line=br.readLine())!=null){
          lastLine=line;
        }
        if(!"invalid".equals(lastLine)){
          validCreds=true;
        }
      }
      catch (FileNotFoundException ex) {
        logger.error("Can not read File: ", SCM_UPDATE_CREDENTIALS_FILE);

      }
      catch (IOException ex) {
        logger.error("Can not read Line in File: ", SCM_UPDATE_CREDENTIALS_FILE);
      }
    }

}
