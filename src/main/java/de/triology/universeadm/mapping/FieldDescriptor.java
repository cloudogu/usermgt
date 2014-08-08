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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class FieldDescriptor<T>
{

  private final Class<T> declaringClass;
  private final Class<?> baseClass;
  private final Class<?> componentType;
  private final Field field;
  private final boolean supported;
  private final boolean array;
  private final boolean list;
  private final boolean set;

  FieldDescriptor(Class<T> declaringClass, Field field)
  {
    this.declaringClass = declaringClass;
    this.field = field;

    boolean s = true;
    Class<?> bc = field.getType();
    Type type = field.getGenericType();
    if (type instanceof Class)
    {
      array = bc.isArray();

      if (array)
      {
        componentType = bc.getComponentType();
        list = false;
        set = false;
      }
      else if (List.class.isAssignableFrom(bc))
      {
        componentType = Object.class;
        list = true;
        set = false;
      }
      else if (Set.class.isAssignableFrom(bc))
      {
        componentType = Object.class;
        list = false;
        set = true;
      }
      else
      {
        componentType = null;
        list = false;
        set = false;
      }
    }
    else if (type instanceof ParameterizedType)
    {
      final ParameterizedType pt = (ParameterizedType) type;
      final Type rawType = pt.getRawType();
      final Type[] typeParams = pt.getActualTypeArguments();
      if ((rawType instanceof Class) && (typeParams.length == 1)
              && (typeParams[0] instanceof Class))
      {
        bc = (Class<?>) rawType;
        componentType = (Class<?>) typeParams[0];

        if (List.class.isAssignableFrom(bc))
        {
          array = false;
          list = true;
          set = false;
        }
        else if (Set.class.isAssignableFrom(bc))
        {
          array = false;
          list = false;
          set = true;
        }
        else
        {
          array = false;
          list = false;
          set = false;
        }
      }
      else
      {
        s = false;
        array = false;
        list = false;
        set = false;
        bc = null;
        componentType = null;
      }
    }
    else
    {
      s = false;
      array = false;
      list = false;
      set = false;
      bc = null;
      componentType = null;
    }
    this.supported = s;
    this.baseClass = bc;
  }
  
  public boolean isSubClassOf(Class<?> other)
  {
    return other.isAssignableFrom(baseClass);
  }

  public Class<T> getDeclaringClass()
  {
    return declaringClass;
  }

  public Class<?> getBaseClass()
  {
    return baseClass;
  }

  public Class<?> getComponentType()
  {
    return componentType;
  }

  public Field getField()
  {
    return field;
  }

  public boolean isSupported()
  {
    return supported;
  }

  public boolean isArray()
  {
    return array;
  }

  public boolean isList()
  {
    return list;
  }

  public boolean isSet()
  {
    return set;
  }

  public boolean isMultiValue(){
    return array || set || list;
  }
  
}
