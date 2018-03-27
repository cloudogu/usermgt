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

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class ClassDescriptorTest
{

  @Test
  public void testSimpleField()
  {
    ClassDescriptor<TestClass> desc = new ClassDescriptor<>(TestClass.class);
    assertEquals(TestClass.class, desc.getType());
    FieldDescriptor<TestClass> field = desc.getField("string");
    assertNotNull(field);
    assertTrue(field.isSupported());
    assertFalse(field.isArray());
    assertFalse(field.isList());
    assertFalse(field.isSet());
    assertFalse(field.isMultiValue());
    assertNull(field.getComponentType());
    assertEquals(TestClass.class, field.getDeclaringClass());
  }
  
  @Test
  public void testListField()
  {
    ClassDescriptor<TestClass> desc = new ClassDescriptor<>(TestClass.class);
    assertEquals(TestClass.class, desc.getType());
    FieldDescriptor<TestClass> field = desc.getField("integers");
    assertNotNull(field);
    assertTrue(field.isSupported());
    assertFalse(field.isArray());
    assertTrue(field.isList());
    assertFalse(field.isSet());
    assertTrue(field.isMultiValue());
    assertEquals(Integer.class, field.getComponentType());
    assertEquals(TestClass.class, field.getDeclaringClass());
  }
  
  @Test
  public void testArrayField()
  {
    ClassDescriptor<TestClass> desc = new ClassDescriptor<>(TestClass.class);
    assertEquals(TestClass.class, desc.getType());
    FieldDescriptor<TestClass> field = desc.getField("floats");
    assertNotNull(field);
    assertTrue(field.isSupported());
    assertTrue(field.isArray());
    assertFalse(field.isList());
    assertFalse(field.isSet());
    assertTrue(field.isMultiValue());
    assertEquals(float.class, field.getComponentType());
    assertEquals(TestClass.class, field.getDeclaringClass());
  }
  
  @Test
  public void testUnsupportedField()
  {
    ClassDescriptor<TestClass> desc = new ClassDescriptor<>(TestClass.class);
    assertEquals(TestClass.class, desc.getType());
    FieldDescriptor<TestClass> field = desc.getField("map");
    assertNotNull(field);
    assertFalse(field.isSupported());
    assertFalse(field.isArray());
    assertFalse(field.isList());
    assertFalse(field.isSet());
    assertFalse(field.isMultiValue());
    assertNull(field.getComponentType());
    assertEquals(TestClass.class, field.getDeclaringClass());
  }
  
  @Test
  public void testNewInstance(){
    ClassDescriptor<TestClass> desc = new ClassDescriptor<>(TestClass.class);
    TestClass test = desc.newInstance();
    assertNotNull(test);
    assertEquals(TestClass.class, test.getClass());
  }
  
  @Test(expected = MappingException.class)
  public void testNewInstanceFailure(){
    ClassDescriptor<TestClass2> desc = new ClassDescriptor<>(TestClass2.class);
    desc.newInstance();
  }

  private static class TestClass2 {
    
  }
  
  
  
  public static class TestClass {
    private String string;
    private List<Integer> integers;
    private float[] floats;
    private Map<String,String> map;
  }
  
}
