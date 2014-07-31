/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ssdorra
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
