/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import com.google.common.base.Charsets;
import java.nio.charset.Charset;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ssdorra
 */
public class LDAPHasherTest
{
  
  private static final Charset charset = Charsets.UTF_8;
  
  @Test
  public void testMD5Hash()
  {
    LDAPHasher h = new LDAPHasher(LDAPHasher.Algorithm.MD5, charset);
    String v = new String(h.hash("test"), charset);
    assertTrue(v.startsWith("{MD5}"));
    String v2 = new String(h.hash("test"), charset);
    assertEquals(v, v2);
  }
  
  @Test
  public void testSSHAHash(){
    LDAPHasher h = new LDAPHasher(LDAPHasher.Algorithm.SSHA, charset);
    String v = new String(h.hash("test"), charset);
    assertTrue(v.startsWith("{SSHA}"));
    String v2 = new String(h.hash("test"), charset);
    // salted value shold not be equals
    assertNotEquals(v, v2);
  }
  
}
