/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.scmmu.usermgm;

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
 * @author ssdorra
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
