/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
    if (type.isSubClassOf(ArrayList.class))
    {
      ArrayList list = new ArrayList(strings.length);
      fill(list, type.getComponentType(), strings);
      result = list;
    }
    else if (type.isSubClassOf(LinkedList.class))
    {
      LinkedList list = new LinkedList();
      fill(list, type.getComponentType(), strings);
      result = list;
    }
    else if (type.isSubClassOf(List.class))
    {
      ArrayList list = new ArrayList(strings.length);
      fill(list, type.getComponentType(), strings);
      result = list;
    }
    else if (type.isSubClassOf(HashSet.class))
    {
      HashSet set = new HashSet(strings.length);
      fill(set, type.getComponentType(), strings);
      result = set;
    }
    else if (type.isSubClassOf(TreeSet.class))
    {
      TreeSet set = new TreeSet();
      fill(set, type.getComponentType(), strings);
      result = set;
    }
    else if (type.isSubClassOf(Set.class))
    {
      HashSet set = new HashSet(strings.length);
      fill(set, type.getComponentType(), strings);
      result = set;
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
