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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultMappingDecoderTest
{
  
  private static final DefaultMappingDecoder decoder = new DefaultMappingDecoder();

  @Test
  public void testDecodeFromString()
  {
    ClassDescriptor<TypeInfo> desc = new ClassDescriptor<>(TypeInfo.class);
    assertEquals("asd", decoder.decodeFromString(desc.getField("string"), "asd"));
    assertEquals(1, decoder.decodeFromString(desc.getField("singleInt"), "1"));
    assertEquals(true, decoder.decodeFromString(desc.getField("singleBoolean"), "true"));
    assertEquals(false, decoder.decodeFromString(desc.getField("singleBoolean"), "false"));
  }
  
  @Test
  public void testDecodeFromMultiString() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException
  {
    ClassDescriptor<TypeInfo> desc = new ClassDescriptor<>(TypeInfo.class);
    
    List<String> stringList = new ArrayList<>();
    stringList.add("asd");
    stringList.add("bds");
    assertEquals(
      stringList, 
      decoder.decodeFromMultiString(
        desc.getField("strings"),
        new String[]{"asd", "bds"}
      )
    );
    
    List<Integer> ints = new ArrayList<>();
    ints.add(1);
    ints.add(3);
    assertEquals(
      ints, 
      decoder.decodeFromMultiString(
        desc.getField("ints"),
        new String[]{"1", "3"}
      )
    );
  }
  
  
  private static class TypeInfo {
    private String string;
    private int singleInt;
    private boolean singleBoolean;
    private List<String> strings;
    private List<Integer> ints;
  }
  
}
