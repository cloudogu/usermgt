/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
 * @author ssdorra
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
  
}
