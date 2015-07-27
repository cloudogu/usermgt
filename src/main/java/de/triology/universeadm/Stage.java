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

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.Locale;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public enum Stage
{
  DEVELOPMENT, PRODUCTION;

  /** Field description */
  private static final String ENV_STAGE = "UNIVERSEADM_STAGE";

  /** Field description */
  private static final String PROPERTY_STAGE = "universeadm.stage";

  /** Field description */
  private static final Stage current;

  /** Field description */
  private static final Logger logger = LoggerFactory.getLogger(Stage.class);

  //~--- static initializers --------------------------------------------------

  static
  {
    String stage = System.getenv(ENV_STAGE);

    if (Strings.isNullOrEmpty(stage))
    {
      stage = System.getProperty(PROPERTY_STAGE);
    }

    if (Strings.isNullOrEmpty(stage))
    {
      current = Stage.PRODUCTION;
    }
    else
    {
      current = Stage.valueOf(stage.toUpperCase(Locale.ENGLISH));
    }
    
    logger.info("start with stage {}", stage);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the current stage of universeadm.
   *
   *
   * @return current stage
   */
  public static Stage get()
  {
    return current;
  }
}
