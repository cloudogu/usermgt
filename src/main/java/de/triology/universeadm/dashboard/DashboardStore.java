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
package de.triology.universeadm.dashboard;

import com.google.inject.Inject;
import de.triology.universeadm.account.DefaultAccountManager;
import de.triology.universeadm.settings.SettingsException;
import de.triology.universeadm.user.User;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mbehlendorf
 */
class DashboardStore {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DashboardStore.class);
  DefaultAccountManager accountManager;
  String dashboardPath = "/etc/scmmu/dashboard/";

  public static void touchFile(File f) {
    try {
      File parent = f.getParentFile();
      if (!parent.exists() && !parent.mkdirs()) {
        throw new SettingsException("could not create directory ".concat(f.getPath()));
      }
      f.createNewFile();
    }
    catch (IOException ex) {
      throw new SettingsException("could not store file ".concat(f.getPath()), ex);
    }
  }

  @Inject
  public DashboardStore(DefaultAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  private File getUserFile() {
    User user = accountManager.getCurrentUser();
    return new File(dashboardPath + user.getUsername() + ".json");
  }

  public String get() {
    String data = "";
    File f = getUserFile();
    if (!f.exists()) {
      f = new File(dashboardPath + "default");
    }
    try {
      data = FileUtils.readFileToString(f);
    }
    catch (IOException ex) {
      logger.error("Cant read File: " + f);
    }
    return data;
  }

  public void set(String data) {
    File f = getUserFile();
    if (!f.exists()) {
      touchFile(f);
    }
    try {
      FileUtils.writeStringToFile(f, data);
    }
    catch (IOException ex) {
      logger.error("Cant write into File: " + f);
    }
  }
}
