/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.template;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public interface Mapper<T>
{
  
  public List<String> getReturningAttributes();
  
  public Entry convert( T object );
  
  public T convert(Entry entry);
  
  public List<Modification> getModifications(T object);
  
}
