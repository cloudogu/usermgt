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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.Caches;
import de.triology.universeadm.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class DefaultMapperFactory implements MapperFactory
{

  /** Field description */
  private static final Logger logger =
    LoggerFactory.getLogger(DefaultMapperFactory.class);

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   *
   * @param converterFactory
   */
  @Inject
  public DefaultMapperFactory(MappingConverterFactory converterFactory)
  {
    this(converterFactory, Stage.get());
  }

  /**
   * Constructs ...
   *
   *
   * @param converterFactory
   * @param stage
   */
  @VisibleForTesting
  DefaultMapperFactory(MappingConverterFactory converterFactory, Stage stage)
  {
    if (stage == Stage.PRODUCTION)
    {
      logger.info("create mapper factory with enabled cache");
      cache = Caches.createSmallCache();
    }
    else
    {
      logger.info("create mapper factory with disabled cache");
      cache = Caches.createDisabledCache();
    }

    this.converterFactory = converterFactory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param type
   * @param parentDN
   * @param <T>
   *
   * @return
   */
  @Override
  public <T> Mapper<T> createMapper(Class<T> type, String parentDN)
  {
    try
    {
      return getOrCreateMapperFromCache(type, parentDN);
    }
    catch (ExecutionException ex)
    {
      throw new MappingException("could not get or create mapper", ex);
    }
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param type
   * @param parentDN
   * @param <T>
   *
   * @return
   *
   * @throws ExecutionException
   */
  @SuppressWarnings("unchecked")
  private <T> Mapper<T> getOrCreateMapperFromCache(final Class<T> type,
    final String parentDN)
    throws ExecutionException
  {
    return cache.get(new CacheKey(type, parentDN), new Callable<Mapper<T>>()
    {

      @Override
      public Mapper<T> call()
      {
        String name =
          type.getSimpleName().toLowerCase(Locale.ENGLISH).concat(".xml");
        Mapping mapping =
          BaseDirectory.getConfiguration("mapping/".concat(name),
            Mapping.class);

        if (mapping == null)
        {
          throw new MappingException("could not find mapping");
        }

        return new DefaultMapper<>(converterFactory, mapping, type, parentDN);
      }
    });
  }

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/08/15
   * @author         Enter your name here...    
   */
  private static class CacheKey
  {

    /**
     * Constructs ...
     *
     *
     * @param clazz
     * @param parentDN
     */
    public CacheKey(Class<?> clazz, String parentDN)
    {
      this.clazz = clazz;
      this.parentDN = parentDN;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param obj
     *
     * @return
     */
    @Override
    public boolean equals(Object obj)
    {
      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      final CacheKey other = (CacheKey) obj;

      return Objects.equal(clazz, other.clazz)
        && Objects.equal(parentDN, other.parentDN);
    }

    /**
     * Method description
     *
     *
     * @return
     */
    @Override
    public int hashCode()
    {
      return Objects.hashCode(clazz, parentDN);
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final Class<?> clazz;

    /** Field description */
    private final String parentDN;
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Cache<CacheKey, Mapper> cache;

  /** Field description */
  private final MappingConverterFactory converterFactory;
}
