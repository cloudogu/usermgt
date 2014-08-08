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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class CipherTest
{

  @Test
  public void testCipher()
  {
    String plaintext = "The Hitchhiker's Guide to the Galaxy";
    String encrypted = Cipher.encrypt(plaintext);
    assertNotEquals(plaintext, encrypted);
    assertEquals(plaintext, Cipher.decrypt(encrypted));
  }

  @Test
  public void testCliWithoutArguments()
  {
    CLIResult r = executeCLI();
    assertEquals(1, r.returnCode);
    assertTrue(r.err.startsWith("usage"));
  }

  @Test
  public void testCliCreateKey()
  {
    CLIResult r = executeCLI("createkey");
    assertEquals(0, r.returnCode);
    assertTrue(r.out.contains("0x"));
  }

  @Test
  public void testEncryptWithoutText()
  {
    CLIResult r = executeCLI("encrypt");
    assertEquals(2, r.returnCode);
    assertTrue(r.err.startsWith("usage"));
  }

  @Test
  public void testEncrypt()
  {
    String plaintext = "The Hitchhiker's Guide to the Galaxy";
    CLIResult r = executeCLI("encrypt", plaintext);
    assertEquals(0, r.returnCode);
    String encrypted = r.out.trim();
    assertNotEquals(plaintext, encrypted);
    assertEquals(plaintext, Cipher.decrypt(encrypted));
  }

  private CLIResult executeCLI(String... cmd)
  {
    ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
    out = new PrintStream(baosOut);
    ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
    err = new PrintStream(baosErr);
    int rc = Cipher.executeCliCommand(out, err, cmd);
    out.close();
    String outString = baosOut.toString();
    String errString = baosErr.toString();
    return new CLIResult(rc, outString, errString);
  }

  private PrintStream out;

  private PrintStream err;

  private static class CLIResult
  {

    private final int returnCode;
    private final String out;
    private final String err;

    public CLIResult(int returnCode, String out, String err)
    {
      this.returnCode = returnCode;
      this.out = out;
      this.err = err;
    }
  }
}
