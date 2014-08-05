/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public class MemberMappingConverter extends AbstractMappingConverter
{
  
  private static final String DUMMY_DN = "cn=__dummy";
  
  private final LDAPConnectionStrategy strategy;
  private final Mapper<User> mapper;

  private static final Logger logger = LoggerFactory.getLogger(MemberMappingConverter.class);
  
  @Inject
  public MemberMappingConverter(LDAPConnectionStrategy strategy, LDAPConfiguration configuration, MapperFactory mapperFactory)
  {
    this.strategy = strategy;
    this.mapper = mapperFactory.createMapper(User.class, configuration.getUserBaseDN());
  }

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
        if (!DUMMY_DN.equals(v))
        {
          collection.add(decodeFromString(null, v));
        }
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
  public String encodeAsString(Object object)
  {
    String dn = null;
    String value = object.toString();
    Filter filter = Filter.createANDFilter(mapper.getBaseFilter(), Filter.createEqualityFilter(mapper.getRDNName(), value));
    try
    {
      SearchResultEntry entry = strategy.get().searchForEntry(mapper.getParentDN(), SearchScope.SUB, filter, "dn");
      if ( entry == null )
      {
        logger.warn("could not find mapping for {}", value);
      }
      else
      {
        dn = entry.getDN();
      }
    }
    catch (LDAPSearchException ex)
    {
      throw new MappingException("search for member dn failed", ex);
    }
    return dn;
  }

  @Override
  public String[] encodeAsMultiString(Object object)
  {
    if (!(object instanceof Iterable))
    {
      throw new MappingException("encoder supports only subtyes of iterabke");
    }
    
    List<String> dns = Lists.newArrayList(DUMMY_DN);
    List<Filter> or = Lists.newArrayList();
    String name = mapper.getRDNName();
    for (String value : (Iterable<String>) object)
    {
      or.add(Filter.createEqualityFilter(name, value));
    }
    Filter filter = Filter.createANDFilter(mapper.getBaseFilter(), Filter.createORFilter(or));
    try
    {
      logger.trace("convert usernames to dns by using filter: {}", filter);
      
      SearchResult result = strategy.get().search(mapper.getParentDN(), SearchScope.SUB, filter, "dn");
      for ( SearchResultEntry e : result.getSearchEntries() )
      {
        dns.add(e.getDN());
      }
    }
    catch (LDAPSearchException ex)
    {
      throw new MappingException("search for member dns failed", ex);
    }
    
    
    return dns.toArray(new String[dns.size()]);
  }
  
  
}
