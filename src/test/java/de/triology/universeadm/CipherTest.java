/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ssdorra
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
  public void testCliCreateKey(){
    CLIResult r = executeCLI("createkey");
    assertEquals(0, r.returnCode);
    assertTrue(r.out.contains("0x"));
  }
  
  @Test
  public void testEncryptWithoutText(){
    CLIResult r = executeCLI("encrypt");
    assertEquals(2, r.returnCode);
    assertTrue(r.err.startsWith("usage"));
  }
  
  @Test
  public void testEncrypt(){
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
