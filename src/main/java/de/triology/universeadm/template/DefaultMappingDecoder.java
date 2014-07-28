/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.template;

import com.google.common.collect.Lists;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.beanutils.ConvertUtils;

/**
 *
 * @author ssdorra
 */
public class DefaultMappingDecoder implements MappingDecoder
{

  @Override
  public Object decodeFromString(Class<?> type, String string)
  {
    return ConvertUtils.convert(string, type);
  }

  @Override
  public Object decodeFromMultiString(Class<?> type, String[] strings)
  {
    Object result = null;
    if (type.isAssignableFrom(Iterable.class))
    {
      Type[] types = ((ParameterizedType) type.getGenericSuperclass()).getActualTypeArguments();
      if (types.length != 1)
      {
        throw new MappingException("supported iterables must have exactly on type parameter e.g. List<String>");
      }
      Class<?> typeParameter = (Class<?>) types[0];
      if (type.isAssignableFrom(ArrayList.class))
      {
        ArrayList list = new ArrayList(strings.length);
        fill(list, typeParameter, strings);
        result = list;
      }
      else if (type.isAssignableFrom(LinkedList.class))
      {
        LinkedList list = new LinkedList();
        fill(list, typeParameter, strings);
        result = list;
      }
      else if (type.isAssignableFrom(List.class) && typeParameter.isAssignableFrom(String.class))
      {
        result = Lists.newArrayList(strings);
      }
      else if (type.isAssignableFrom(List.class))
      {
        ArrayList list = new ArrayList(strings.length);
        fill(list, typeParameter, strings);
        result = list;
      } 
      else 
      {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    }
    else if (type.isArray())
    {
      Class<?> ctype = type.getComponentType();
      if (ctype.isAssignableFrom(String.class)){
        result = Arrays.copyOf(strings, strings.length);
      } else {
        result = Array.newInstance(ctype, strings.length);
        for (int i = 0; i < strings.length; i++)
        {
          Array.set(result, i, decodeFromString(ctype, strings[i]));
        }
      }
    }
    return result;
  }

  private void fill(Collection collection, Class<?> type, String[] values)
  {
    for (String value : values)
    {
      collection.add(decodeFromString(type, value));
    }
  }

  @Override
  public Object decodeFromBytes(Class<?> type, byte[] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object decodeFromMultiBytes(Class<?> type, byte[][] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
