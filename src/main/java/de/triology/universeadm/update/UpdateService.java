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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import de.triology.universeadm.settings.SettingsException;
import static de.triology.universeadm.update.UpdateConstants.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 *
 * @author mbehlendorf
 */
public class UpdateService {

    @VisibleForTesting
    static final String DEFAULT_UPDATE_WEBSITE = "http://192.168.115.80/applupdateservice/applupdate.php";

    public void startUpdate() {
        
        touchFlag(SCM_UPDATE_EXECUTOR_FLAG);
    }
    
    public Status checkUpdate() {
        return new Status();
        
    }
    
    public void touchFlag(File flag) {
        try {
            File parent = flag.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new SettingsException("could not create directory ".concat(flag.getPath()));
            }
            flag.createNewFile();

        } catch (IOException ex) {
            throw new SettingsException("could not store file ".concat(flag.getPath()), ex);
        }

    }   
    
}
