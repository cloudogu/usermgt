/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ssdorra
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
