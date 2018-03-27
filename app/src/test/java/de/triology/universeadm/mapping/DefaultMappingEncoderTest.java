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

package de.triology.universeadm.mapping;

import com.google.common.collect.Lists;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultMappingEncoderTest
{
  
  private static final DefaultMappingEncoder encoder = new DefaultMappingEncoder();
  
  @Test
  public void testEncodeAsString()
  {
    assertEquals("asd", encoder.encodeAsString("asd"));
    assertEquals("1", encoder.encodeAsString(1));
    assertEquals("1", encoder.encodeAsString(1l));
    assertEquals("2.3", encoder.encodeAsString(2.3f));
    assertEquals("true", encoder.encodeAsString(true));
  }
  
  @Test
  public void testEncodeAsMultiString()
  {
    assertArrayEquals(
      new String[]{"1", "2", "3"}, 
      encoder.encodeAsMultiString(new int[]{1, 2, 3})
    );
    assertArrayEquals(
      new String[]{"true", "false", "true"}, 
      encoder.encodeAsMultiString(new boolean[]{true, false, true})
    );
    assertArrayEquals(
      new String[]{"12.0", "13.0", "14.42"}, 
      encoder.encodeAsMultiString(Lists.newArrayList(12d, 13d, 14.42d))
    );
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void testEncodeAsBytes(){
    encoder.encodeAsBytes(new Object());
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void testEncodeAsMultiBytes(){
    encoder.encodeAsMultiBytes(new Object());
  }
  
}
