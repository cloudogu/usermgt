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
import java.lang.reflect.Array;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class DefaultMappingEncoder extends AbstractMappingEncoder
{

  private static final Logger logger = LoggerFactory.getLogger(DefaultMappingEncoder.class);

  @Override
  public String encodeAsString(Object object)
  {
    return object != null ? object.toString() : null;
  }

  @Override
  public String[] encodeAsMultiString(Object object)
  {
    List<String> values = Lists.newArrayList();
    if (object instanceof Iterable)
    {
      for (Object o : (Iterable) object)
      {
        values.add(o.toString());
      }
    }
    else if (object.getClass().isArray())
    {
      Class<?> type = object.getClass().getComponentType();
      if (type.equals(Object.class))
      {
        for (Object o : (Object[]) object){
          values.add(o.toString());
        }
      }
      else
      {
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++)
        {
          values.add(Array.get(object, i).toString());
        }
      }
    }
    else
    {
      logger.debug("object {} is not an instance of iterable and is not an array", object.getClass());
      values.add(object.toString());
    }
    return values.toArray(new String[values.size()]);
  }

}
