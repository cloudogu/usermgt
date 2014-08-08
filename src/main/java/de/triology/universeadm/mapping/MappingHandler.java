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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import static de.triology.universeadm.AbstractLDAPManager.EQUAL;
import static de.triology.universeadm.AbstractLDAPManager.WILDCARD;
import de.triology.universeadm.EntityAlreadyExistsException;
import de.triology.universeadm.EntityException;
import de.triology.universeadm.EntityNotFoundException;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.LDAPUtil;
import de.triology.universeadm.validation.Validator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 *
 * @param <T>
 */
public class MappingHandler<T extends Comparable<T>>
{

  private static final Logger logger = LoggerFactory.getLogger(MappingHandler.class);

  private final LDAPConnectionStrategy strategy;
  private final Mapper<T> mapper;
  private final Validator validator;
  private final String[] returningAttributes;

  public MappingHandler(LDAPConnectionStrategy strategy, Mapper<T> mapper, Validator validator)
  {
    this.strategy = strategy;
    this.mapper = mapper;
    this.validator = validator;
    List<String> rattrs = this.mapper.getReturningAttributes();
    this.returningAttributes = rattrs.toArray(new String[rattrs.size()]);
  }

  public void create(T object)
  {
    String id = mapper.getRDNValue(object);
    logger.info("create entry {}", id);
    validator.validate(object, "entity is not valid");
    Entry entry = mapper.convert(object);
    try
    {
      entry = consume(object, entry);
      if (entry != null)
      {
        if (logger.isTraceEnabled())
        {
          logger.trace("create ldap entry:\n{}", LDAPUtil.toLDIF(entry));
        }
        strategy.get().add(entry);
      }
      else
      {
        logger.debug("consumer returned null entry");
      }
    }
    catch (LDAPException ex)
    {
      if (ex.getResultCode() == ResultCode.ENTRY_ALREADY_EXISTS)
      {
        throw new EntityAlreadyExistsException(
                String.format("entity %s already exists", id), ex);
      }
      else
      {
        throw new EntityException("could not create entity ".concat(entry.getDN()), ex);
      }
    }
  }

  public void modify(T object)
  {
    String id = mapper.getRDNValue(object);
    logger.info("update entity {}", id);
    validator.validate(object, "entity is not valid");
    List<Modification> modifications = mapper.getModifications(object);
    try
    {
      modifications = consume(object, modifications);
      if (modifications != null && !modifications.isEmpty())
      {
        String dn = searchDN(id);
        if (logger.isTraceEnabled())
        {
          logger.trace("modify ldap entry:\n{}", LDAPUtil.toLDIF(dn, modifications));
        }
        strategy.get().modify(dn, modifications);
      }
      else
      {
        logger.debug("modifications are empty after consume");
      }
    }
    catch (LDAPException ex)
    {
      if (ex.getResultCode() == ResultCode.NO_SUCH_OBJECT)
      {
        throw new EntityNotFoundException("could not find entity ".concat(id), ex);
      }
      else
      {
        throw new EntityException("could not modify entity ".concat(id), ex);
      }
    }
  }

  public void remove(T object)
  {
    String id = mapper.getRDNValue(object);
    logger.info("remove entity with id {}", id);
    try
    {
      String dn = searchDN(id);
      logger.trace("remove ldap entry {}", dn);
      strategy.get().delete(dn);
    }
    catch (LDAPException ex)
    {
      if (ex.getResultCode() == ResultCode.NO_SUCH_OBJECT)
      {
        throw new EntityNotFoundException("could not find entity ".concat(id), ex);
      }
      else
      {
        throw new EntityException("could not remove entity ".concat(id), ex);
      }
    }
  }
  
  private Filter createObjectFilter(String id)
  {
    return Filter.createANDFilter(
      mapper.getBaseFilter(), 
      Filter.createEqualityFilter(mapper.getRDNName(), id)
    );
  }
  
  private Entry searchForObject(String id, String... attributes) throws LDAPSearchException
  {
    Filter filter = createObjectFilter(id);
    return strategy.get().searchForEntry(mapper.getParentDN(), SearchScope.SUB, filter, attributes);
  }
  
  private String searchDN(String id) throws LDAPSearchException{
    Entry entry = searchForObject(id, mapper.getRDNName());
    if ( entry == null ){
      throw new EntityNotFoundException("could not find entity with id ".concat(id));
    }
    return entry.getDN();
  }

  public T get(String id)
  {
    Preconditions.checkNotNull(id, "id is required");
    T entity = null;
    try
    { 
      Entry e = searchForObject(id, returningAttributes);
      if (e != null)
      {
        T object = consume(e, mapper.convert(e));
        if (object != null)
        {
          entity = object;
        }
        else
        {
          logger.debug("consumer returned null for object {}", e.getDN());
        }
      }
    }
    catch (LDAPSearchException ex)
    {
      throw new EntityException("could not get entity for ".concat(id), ex);
    }

    return entity;
  }

  public List<T> getAll()
  {
    final List<T> entities = Lists.newArrayList();

    try
    {
      SearchResult result = strategy.get().search(mapper.getParentDN(), SearchScope.SUB, mapper.getBaseFilter(), returningAttributes);
      for (SearchResultEntry e : result.getSearchEntries())
      {
        consume(entities, e);
      }
    }
    catch (LDAPSearchException ex)
    {
      throw new EntityException("could not get all entities", ex);
    }

    Collections.sort(entities);
    return ImmutableList.copyOf(entities);
  }

  public List<T> search(String query)
  {
    String q = WILDCARD.concat(query).concat(WILDCARD);
    Filter base = mapper.getBaseFilter();

    List<T> entities = Lists.newArrayList();
    try
    {
      List<Filter> or = Lists.newArrayList();
      for (String attribute : mapper.getSearchAttributes())
      {
        or.add(Filter.create(attribute.concat(EQUAL).concat(q)));
      }
      Filter filter = Filter.createANDFilter(base, Filter.createORFilter(or));
      logger.debug("start entity search with filter {}", filter);

      SearchResult result = strategy.get().search(mapper.getParentDN(), SearchScope.SUB, filter, returningAttributes);
      for (SearchResultEntry e : result.getSearchEntries())
      {
        consume(entities, e);
      }
    }
    catch (LDAPException ex)
    {
      throw new EntityException("could not search entities with query: ".concat(query), ex);
    }

    Collections.sort(entities);

    return ImmutableList.copyOf(entities);
  }

  protected List<Modification> consume(T object, List<Modification> mods)
  {
    return mods;
  }

  protected Entry consume(T object, Entry entry)
  {
    return entry;
  }

  protected T consume(Entry entry, T object)
  {
    return object;
  }

  private void consume(Collection<T> collection, Entry entry)
  {
    T object = consume(entry, mapper.convert(entry));
    if (object != null)
    {
      collection.add(object);
    }
    else
    {
      logger.debug("consumer returned null for object {}", entry.getDN());
    }
  }

}
