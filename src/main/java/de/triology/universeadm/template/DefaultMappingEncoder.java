/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.template;

import com.google.common.collect.Lists;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
public final class DefaultMappingEncoder implements MappingEncoder
{

  private static final Logger logger = LoggerFactory.getLogger(DefaultMappingEncoder.class);
  
  @Override
  public String encodeAsString(Object object)
  {
    return object.toString();
  }

  @Override
  public String[] encodeAsMultiString(Object object)
  {
    List<String> values = Lists.newArrayList();
    if ( object instanceof Iterable)
    {
      for ( Object o : (Iterable) object )
      {
        values.add(o.toString());
      }
    } 
    else if ( object.getClass().isArray() )
    {
      for ( Object o : (Iterable) object )
      {
        values.add(o.toString());
      }
    } 
    else 
    {
      logger.debug("object {} is not an instance of iterable and is not an array", object.getClass());
      values.add(object.toString());
    }
    return values.toArray(new String[values.size()]);
  }

  @Override
  public byte[] encodeAsBytes(Object object)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public byte[][] encodeAsMultiBytes(Object object)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
