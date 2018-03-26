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

import com.google.common.annotations.VisibleForTesting;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class PathXmlAdapter extends XmlAdapter<String, File>
{

  /** Field description */
  private static final String BASEDIR = "{basedir}";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public PathXmlAdapter()
  {
    this(BaseDirectory.get());
  }

  /**
   * Constructs ...
   *
   *
   * @param baseDirectory
   */
  @VisibleForTesting
  PathXmlAdapter(File baseDirectory)
  {
    this.baseDirectory = baseDirectory.getAbsolutePath();
  }

  /**
   * Constructs ...
   *
   *
   * @param baseDirectory
   */
  @VisibleForTesting
  PathXmlAdapter(String baseDirectory)
  {
    this.baseDirectory = baseDirectory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param v
   *
   * @return
   */
  @Override
  public String marshal(File v)
  {
    return v.getAbsolutePath();
  }

  /**
   * Method description
   *
   *
   * @param v
   *
   * @return
   */
  @Override
  public File unmarshal(String v)
  {
    return new File(v.replace(BASEDIR, baseDirectory));
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final String baseDirectory;
}
