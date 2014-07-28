/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ssdorra
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
