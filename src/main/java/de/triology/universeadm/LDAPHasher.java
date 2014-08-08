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

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.unboundid.util.Base64;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class LDAPHasher
{

  public static enum Algorithm
  {

    SSHA("SHA", true, 20), SHA("SHA", false, 20), MD5("MD5", false, 20);

    private final String baseName;
    private final boolean salted;
    private final int length;

    private Algorithm(String baseName, boolean salted, int length)
    {
      this.baseName = baseName;
      this.salted = salted;
      this.length = length;
    }

    public String getBaseName()
    {
      return baseName;
    }

    public boolean isSalted()
    {
      return salted;
    }

  }

  private final Charset charset;

  private final Algorithm algorithm;

  public LDAPHasher(Algorithm algorithm, Charset charset)
  {
    Preconditions.checkNotNull(algorithm);
    this.algorithm = algorithm;
    this.charset = charset;
  }

  private static final int SALT_LENGTH = 4;

  private byte[] createSalt()
  {
    Random random = new Random();
    byte[] bytes = new byte[SALT_LENGTH];
    random.nextBytes(bytes);
    return bytes;
  }

  public LDAPHasher()
  {
    this(Algorithm.SSHA, Charsets.UTF_8);
  }

  public byte[] hash(String value)
  {
    StringBuilder buffer = new StringBuilder();
    buffer.append("{");
    buffer.append(algorithm.name());
    buffer.append("}");
    buffer.append(createHash(value));
    return buffer.toString().getBytes(charset);
  }

  private String createHash(String value)
  {
    MessageDigest digest = createDigest();
    byte[] bytes = value.getBytes(charset);
    digest.update(bytes);
    byte[] hashed;
    if (algorithm.isSalted())
    {
      byte[] salt = createSalt();
      digest.update(salt);
      byte[] digestBytes = digest.digest();
      hashed = new byte[salt.length + algorithm.length];
      System.arraycopy(digestBytes, 0, hashed, 0, digestBytes.length);
      System.arraycopy(salt, 0, hashed, digestBytes.length, salt.length);
    } else
    {
      hashed = digest.digest();
    }

    return Base64.encode(hashed);
  }

  private MessageDigest createDigest()
  {
    MessageDigest digest;
    try
    {
      digest = MessageDigest.getInstance(algorithm.getBaseName());
      digest.reset();
    } catch (NoSuchAlgorithmException ex)
    {
      // should never happen
      throw Throwables.propagate(ex);
    }
    return digest;
  }
}
