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

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
final class ClassDescriptor<T>
{

  private final Class<T> type;
  private final Map<String,FieldDescriptor<T>> fields;
  
  ClassDescriptor(Class<T> type)
  {
    this.type = type;
    ImmutableMap.Builder<String,FieldDescriptor<T>> builder = ImmutableMap.builder();
    Class<?> c = type;
    while ( c != null )
    {
      for ( Field f : c.getDeclaredFields())
      {
        builder.put(f.getName(), new FieldDescriptor<>(type, f));
      }
      c = c.getSuperclass();
    }
    
    fields = builder.build();
  }
  
  public T newInstance(){
    try {
      return type.newInstance();
    } catch (IllegalAccessException | InstantiationException ex){
      throw new MappingException("could not create new instance", ex);
    }
  }
  
  public FieldDescriptor<T> getField(String name){
    return fields.get(name);
  }

  public Map<String, FieldDescriptor<T>> getFields()
  {
    return fields;
  }

  public Class<T> getType()
  {
    return type;
  }
  
}
