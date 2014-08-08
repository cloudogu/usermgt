/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
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
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
