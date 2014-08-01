/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import com.google.common.collect.Lists;
import java.lang.reflect.Array;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
public final class DefaultMappingEncoder extends AbstractMappingEncoder
{

  private static final Logger logger = LoggerFactory.getLogger(DefaultMappingEncoder.class);

  @Override
  public String encodeAsString(Object object)
  {
    return object != null ? object.toString() : null;
  }

  @Override
  public String[] encodeAsMultiString(Object object)
  {
    List<String> values = Lists.newArrayList();
    if (object instanceof Iterable)
    {
      for (Object o : (Iterable) object)
      {
        values.add(o.toString());
      }
    }
    else if (object.getClass().isArray())
    {
      Class<?> type = object.getClass().getComponentType();
      if (type.equals(Object.class))
      {
        for (Object o : (Object[]) object){
          values.add(o.toString());
        }
      }
      else
      {
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
        {
          values.add(Array.get(object, i).toString());
        }
      }
    }
    else
    {
      logger.debug("object {} is not an instance of iterable and is not an array", object.getClass());
      values.add(object.toString());
    }
    return values.toArray(new String[values.size()]);
  }

}
