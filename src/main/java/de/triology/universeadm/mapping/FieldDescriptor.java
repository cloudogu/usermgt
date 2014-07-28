/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ssdorra
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
