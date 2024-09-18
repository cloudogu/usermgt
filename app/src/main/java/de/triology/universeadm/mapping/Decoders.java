package de.triology.universeadm.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Decoders
{
  
  private Decoders(){}
  
  static <T> Collection createCollection(FieldDescriptor<T> type, int length)
  {
    Collection collection = null;
    if (type.isSubClassOf(ArrayList.class))
    {
      collection = new ArrayList(length);
    }
    else if (type.isSubClassOf(LinkedList.class))
    {
      collection = new LinkedList();
    }
    else if (type.isSubClassOf(List.class))
    {
      collection = new ArrayList(length);
    }
    else if (type.isSubClassOf(HashSet.class))
    {
      collection = new HashSet(length);
    }
    else if (type.isSubClassOf(TreeSet.class))
    {
      collection = new TreeSet();
    }
    else if (type.isSubClassOf(Set.class))
    {
      collection = new HashSet(length);
    }
    return collection;
  }
  
  static String decodeFromString(String string)
  {
    int i = string.indexOf('=');
    if ( i <= 0 || i+1 >= string.length() )
    {
      throw new MappingException("string " + string + " is not a valid dn, no equal found");
    }
    int j = string.indexOf(',');
    if ( j <= 0 || j <= (i+1) || j >= string.length() )
    {
      throw new MappingException("string " + string + " is not a valid dn, no or misplaced comma found");
    }
    return string.substring(i+1, j);
  }
  
}
