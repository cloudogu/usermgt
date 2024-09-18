package de.triology.universeadm.mapping;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.Modification;
import java.util.List;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @param <T>
 */
public interface Mapper<T>
{
  public String getParentDN();
  
  public Filter getBaseFilter();
  
  public List<String> getSearchAttributes();
  public MappingAttribute getAttribute(String name);

  public Attribute getObjectClasses();
  
  public String getRDNName();
  
  public String getRDNValue( T object );
  
  public List<String> getReturningAttributes();
  
  public Entry convert( T object );
  
  public T convert(Entry entry);
  
  public List<Modification> getModifications(T object);
  
}
