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
