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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.group.Group;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mbehlendorf
 */
@Singleton
public class MemberOfMappingConverter extends AbstractMappingConverter{
    
    private final LDAPConnectionStrategy strategy;
    private final Mapper<Group> mapper;
    private static final Logger logger = LoggerFactory.getLogger(MemberOfMappingConverter.class);
    
     @Inject
    public MemberOfMappingConverter(LDAPConnectionStrategy strategy, LDAPConfiguration configuration, MapperFactory mapperFactory)
    {
    this.strategy = strategy;
    this.mapper = mapperFactory.createMapper(Group.class, configuration.getGroupBaseDN());
    }

    @Override
    public String[] encodeAsMultiString(Object object) {
       return Encoders.encodeAsMultiString(strategy, mapper, object);
    }

    @Override
    public String encodeAsString(Object object) {
        return Encoders.encodeAsString(strategy, mapper, object);
    }

    @Override
    public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings) {
        Collection collection;
    if ( type.isSubClassOf(Collection.class) )
    {
      collection = Decoders.createCollection(type, strings.length);
      for ( String v : strings )
      {
        
          Object memberOf = decodeFromString(null, v);
          if ( collection.contains(memberOf) )
          {
            logger.warn("dublicate memberOf {} found", memberOf);
          }
          else 
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
    public <T> Object decodeFromString(FieldDescriptor<T> type, String string) {
        return Decoders.decodeFromString(string);
    }
  
  
}
