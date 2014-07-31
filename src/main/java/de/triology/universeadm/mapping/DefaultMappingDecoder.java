/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.beanutils.ConvertUtils;

/**
 *
 * @author ssdorra
 */
public class DefaultMappingDecoder implements MappingDecoder
{

  @Override
  public <T> Object decodeFromString(FieldDescriptor<T> type, String string)
  {
    return ConvertUtils.convert(string, type.getBaseClass());
  }

  @Override
  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings)
  {
    Object result = null;
    if (type.isSubClassOf(Collection.class))
    {
      Collection collection = Decoders.createCollection(type, strings.length);
      fill(collection, type.getComponentType(), strings);
      result = collection;
    }
    else if (type.isArray())
    {
      Class<?> ctype = type.getComponentType();
      if (ctype.isAssignableFrom(String.class))
      {
        result = Arrays.copyOf(strings, strings.length);
      }
      else
      {
        result = Array.newInstance(ctype, strings.length);
        for (int i = 0; i < strings.length; i++)
        {
          Array.set(result, i, ConvertUtils.convert(strings[i], ctype));
        }
      }
    }
    else
    {
      throw new MappingException("could not decode field");
    }

    return result;
  }

  private void fill(Collection collection, Class<?> type, String[] values)
  {
    for (String value : values)
    {
      collection.add(ConvertUtils.convert(value, type));
    }
  }

  @Override
  public <T> Object decodeFromBytes(FieldDescriptor<T> type, byte[] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public <T> Object decodeFromMultiBytes(FieldDescriptor<T> type, byte[][] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
