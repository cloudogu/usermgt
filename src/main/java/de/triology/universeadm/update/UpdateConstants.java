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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author mbehlendorf
 */
public final class UpdateConstants {

  /**
   * Path to the configuration files. @since 13.9.3
   */
  static final File SCM_UPDATE_DIRECTORY = new File("/tmp/scmUpdDir");
  /**
   * Path to "Update in progress" flag.
   */
  static final File SCM_UPDATE_IN_PROGRESS_FLAG = new File(SCM_UPDATE_DIRECTORY + "/scmUpdateInProgress");
  /**
   * Path to "update should be executed" flag.
   */
  static final File SCM_UPDATE_EXECUTOR_FLAG = new File(SCM_UPDATE_DIRECTORY + "/scmUpdateExecutor");
  /**
   * Path to "update available" flag.
   */
  static final File SCM_UPDATE_AVAILABLE_FLAG = new File(SCM_UPDATE_DIRECTORY + "/updateAvailable");
  /**
   * Path to "wrong credentials" flag.
   */
  static final File SCM_UPDATE_WRONG_CREDENTIALS_FLAG = new File(SCM_UPDATE_DIRECTORY + "/wrongUserOrPassword");
  /**
   * Path to "up-to-date" flag.
   */
  static final File SCM_UPDATE_NO_UPDATE_FOR_VERSION_FLAG = new File(SCM_UPDATE_DIRECTORY + "/noUpdateForVersion");
  /**
   * Path to "update process not valid" flag.
   */
  static final File SCM_UPDATE_NO_FILE_FOR_VERSION_FLAG = new File(SCM_UPDATE_DIRECTORY + "/noFileForVersion");
  /**
   * Path to "activate check for updates" flag.
   */
  static final File SCM_UPDATE_ACTIVATE_CHECK_FOR_UPDATES_FLAG = new File(SCM_UPDATE_DIRECTORY + "/switchUPDon");
  /**
   * Path to "deactivate check for updates" flag.
   */
  static final File SCM_UPDATE_DISABLE_CHECK_FOR_UPDATES_FLAG = new File(SCM_UPDATE_DIRECTORY + "/switchUPDoff");
  /**
   * Path to "update success" flag.
   */
  static final File SCM_UPDATE_SUCCESS_FLAG = new File(SCM_UPDATE_DIRECTORY + "/UPDSUCC");
  /**
   * Path to "update failed" flag.
   */
  static final File SCM_UPDATE_FAIL_FLAG = new File(SCM_UPDATE_DIRECTORY + "/UPDFAIL");
  /**
   * Path to "update bugzilla plugin configuration"
   */
  static final File SCM_BUGZILLA_PLUGIN_FLAG = new File(SCM_UPDATE_DIRECTORY + "/scmbugplug");
  /**
   * Path to "update cas server configuration" @since 14.4
   */
  static final File SCM_CAS_SERVER_FLAG = new File(SCM_UPDATE_DIRECTORY + "/scmcasupdt");
  /**
   * Path to "pre update check running" flag. @since 13.9.3
   */
  static final File SCM_UPDATE_PRECHECK_RUNNING_FLAG = new File(SCM_UPDATE_DIRECTORY + "/preCheckRunning");
  /**
   * Path to "pre update check done" flag. @since 13.9.3
   */
  static final File SCM_UPDATE_PRECHECK_DONE_FLAG = new File(SCM_UPDATE_DIRECTORY + "/preCheckDone");
  /**
   * Path to "update pre check" file. @since 13.9.3
   */
  static final File SCM_UPDATE_PRECHECK_RESULT_FILE = new File(SCM_UPDATE_DIRECTORY + "/preCheckResult.json");
  /**
   * Path to "user descision needed to proceed with update" flag. @since 13.9.3
   */
  static final File SCM_UPDATE_PRECHECK_USER_DECISION_NEEDED_FLAG = new File(SCM_UPDATE_DIRECTORY + "/userDecision");
  /**
   * Path to the update cronjob.
   */
  static final File SCM_UPDATE_CRONJOB = new File("/etc/cron.d/scmUpdateTest");
  /**
   * Path to the credentials file.
   */
  static final File SCM_UPDATE_CREDENTIALS_FILE = new File("/etc/scmcreds");
  /**
   * Path to file containing actual version name.
   */
  static final File SCM_ACTUAL_VERSION_FILE = new File("/etc/scmissuename");
  /**
   * Hostname of the SCM Managers Website (@since 13.9.3 as fallback)
   */
  static final File SCM_UPDATE_WEBSITE = new File("https://www.scm-manager.com/applupdateservice/applupdate.php");
  /**
   * Path to file containing the update server URL. @since 13.9.3)
   */
  static final File SCM_UPDATE_WEBSITE_FILE = new File(SCM_UPDATE_DIRECTORY + "/updateserver");
  /**
   * Path to file where to write user input. @since 13.9.3
   */
  static final File SCM_UPDATE_FORM_DATA_OUTPUT_FILE = new File(SCM_UPDATE_DIRECTORY + "/inputFile.json");
  /**
   * Path to "user content required" flag. @since 13.9.3
   */
  static final File SCM_UPDATE_FORM_AVAILABLE_FLAG = new File(SCM_UPDATE_DIRECTORY + "/getUserContent");
  /**
   * Path to user input form data.
   */
  static final File SCM_UPDATE_FORM_DATA = SCM_UPDATE_FORM_AVAILABLE_FLAG;
  /**
   * Date/Time format for message output.
   */
  static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

  private UpdateConstants() {

  }

}
