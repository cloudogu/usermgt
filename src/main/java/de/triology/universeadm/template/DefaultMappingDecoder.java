/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.template;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author ssdorra
 */
public class DefaultMappingDecoder implements MappingDecoder
{

  @Override
  public Object decodeFromString(Class<?> type, String string)
  {
    Object value = null;
    if (type.isAssignableFrom(AtomicInteger.class))
    {
      value = new AtomicInteger(Integer.valueOf(string));
    }
    else if (type.isAssignableFrom(AtomicLong.class))
    {
      value = new AtomicLong(Long.valueOf(string));
    }
    else if (type.isAssignableFrom(BigDecimal.class))
    {
      value = new BigDecimal(string);
    }
    else if (type.isAssignableFrom(BigInteger.class))
    {
      value = new BigInteger(string);
    }
    else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(Double.TYPE))
    {
      value = Double.valueOf(string);
    }
    else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(Float.TYPE))
    {
      value = Float.valueOf(string);
    }
    else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(Integer.TYPE))
    {
      value = Integer.valueOf(string);
    }
    else if (type.isAssignableFrom(Long.class) || type.isAssignableFrom(Long.TYPE))
    {
      value = Long.valueOf(string);
    }
    else if (type.isAssignableFrom(Short.class) || type.isAssignableFrom(Short.TYPE))
    {
      value = Short.valueOf(string);
    }
    else if (type.isAssignableFrom(String.class))
    {
      value = String.valueOf(string);
    }
    else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(Boolean.TYPE))
    {
      final String s = string;
      if (s.equalsIgnoreCase("TRUE"))
      {
        value = Boolean.TRUE;
      }
      else if (s.equalsIgnoreCase("FALSE"))
      {
        value = Boolean.FALSE;
      }
      else
      {
        throw new MappingException("boolean value is not true or falce");
      }
    }
    return value;
  }

  @Override
  public Object decodeFromMultiString(Class<?> type, List<String> strings)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Object decodeFromBytes(Class<?> type, byte[] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Object decodeFromMultiBytes(Class<?> type, List<byte[]> bytes)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
