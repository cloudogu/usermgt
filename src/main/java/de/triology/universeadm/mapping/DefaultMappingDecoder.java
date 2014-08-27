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

//~--- non-JDK imports --------------------------------------------------------

import org.apache.commons.beanutils.ConvertUtils;

//~--- JDK imports ------------------------------------------------------------

import java.lang.reflect.Array;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultMappingDecoder extends AbstractMappingDecoder
{

  /**
   * Method description
   *
   *
   * @param type
   * @param strings
   * @param <T>
   *
   * @return
   */
  @Override
  public <T> Object decodeFromMultiString(FieldDescriptor<T> type,
    String[] strings)
  {
    Object result = null;

    if (type.isSubClassOf(Collection.class))
    {
      Collection collection = Decoders.createCollection(type, strings.length);

      fill(collection, type.getComponentType(), strings);
      result = collection;
    }
    else if (type.isArray())
    {
      Class<?> ctype = type.getComponentType();

      if (ctype.isAssignableFrom(String.class))
      {
        result = Arrays.copyOf(strings, strings.length);
      }
      else
      {
        result = Array.newInstance(ctype, strings.length);

        for (int i = 0; i < strings.length; i++)
        {
          Array.set(result, i, ConvertUtils.convert(strings[i], ctype));
        }
      }
    }
    else
    {
      throw new MappingException("could not decode field");
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param type
   * @param string
   * @param <T>
   *
   * @return
   */
  @Override
  public <T> Object decodeFromString(FieldDescriptor<T> type, String string)
  {
    Object result = null;

    if (isNotEmpty(string))
    {
      result = ConvertUtils.convert(string, type.getBaseClass());
    }

    return result;
  }

  /**
   * Method description
   *
   *
   * @param collection
   * @param type
   * @param values
   */
  @SuppressWarnings("unchecked")
  private void fill(Collection collection, Class<?> type, String[] values)
  {
    for (String value : values)
    {
      if (isNotEmpty(value))
      {
        collection.add(ConvertUtils.convert(value, type));
      }
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param string
   *
   * @return
   */
  private boolean isNotEmpty(String string)
  {
    return (string != null) && (string.trim().length() > 0);
  }
}
