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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class InjectorMappingConverterFactory implements MappingConverterFactory
{

  private static final Cache<MappingAttribute, MappingEncoder> encoderCache = CacheBuilder.newBuilder().build();

  private static final Cache<MappingAttribute, MappingDecoder> decoderCache = CacheBuilder.newBuilder().build();
  
  private final Injector injector;
  
  @Inject
  public InjectorMappingConverterFactory(Injector injector)
  {
    this.injector = injector;
  }

  @Override
  public MappingEncoder getEncoder(MappingAttribute attribute)
  {
    try
    {
      return getOrLoadEncoder(injector, attribute);
    }
    catch (ExecutionException ex)
    {
      throw new MappingException("could not get or load encoder", ex);
    }
  }

  @Override
  public MappingDecoder getDecoder(MappingAttribute attribute)
  {
    try
    {
      return getOrLoadDecoder(injector, attribute);
    }
    catch (ExecutionException ex)
    {
      throw new MappingException("could not get or load decoder", ex);
    }
  }

  private static MappingDecoder getOrLoadDecoder(final Injector injector, final MappingAttribute ma) throws ExecutionException
  {
    return decoderCache.get(ma, new Callable<MappingDecoder>()
    {

      @Override
      public MappingDecoder call() throws InstantiationException, IllegalAccessException
      {
        return injector.getInstance(ma.getDecoder());
      }
    });
  }

  private static MappingEncoder getOrLoadEncoder(final Injector injector, final MappingAttribute ma) throws ExecutionException
  {
    return encoderCache.get(ma, new Callable<MappingEncoder>()
    {

      @Override
      public MappingEncoder call() throws InstantiationException, IllegalAccessException
      {
        return injector.getInstance(ma.getEncoder());
      }
    });
  }
  
}
