/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ssdorra
 */
public class DNToValueMappingDecoder implements MappingDecoder
{

  @Override
  public <T> Object decodeFromString(FieldDescriptor<T> type, String string)
  {
    int i = string.indexOf('=');
    if ( i <= 0 || i+1 >= string.length() )
    {
      throw new MappingException("string is not a valid dn, no equal found");
    }
    int j = string.indexOf(',');
    if ( j <= 0 || j <= (i+1) || j >= string.length() )
    {
      throw new MappingException("string is not a valid dn, no or misplaced comma found");
    }
    return string.substring(i+1, j);
  }

  @Override
  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings)
  {
    Collection collection;
    if ( type.isSubClassOf(Collection.class) )
    {
      collection = Decoders.createCollection(type, strings.length);
      for ( String v : strings )
      {
        collection.add(decodeFromString(null, v));
      }
      if ( type.isList() )
      {
        Collections.sort((List) collection);
      }
    } 
    else 
    {
      throw new MappingException("decoder supports only subtypes of collection");
    }
    return collection;
  }

  @Override
  public <T> Object decodeFromBytes(FieldDescriptor<T> type, byte[] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <T> Object decodeFromMultiBytes(FieldDescriptor<T> type, byte[][] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
}
