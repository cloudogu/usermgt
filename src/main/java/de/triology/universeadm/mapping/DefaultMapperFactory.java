/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.BaseDirectory;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public class DefaultMapperFactory implements MapperFactory
{

  private final Cache<CacheKey, Mapper> cache;
  private final MappingConverterFactory converterFactory;

  private static final Logger logger = LoggerFactory.getLogger(DefaultMapperFactory.class);

  @VisibleForTesting
  static final boolean CACHE_DISABLED = Boolean.getBoolean(DefaultMapperFactory.class.getName().concat(".disable-cache"));

  @Inject
  public DefaultMapperFactory(MappingConverterFactory converterFactory)
  {
    if (!CACHE_DISABLED)
    {
      logger.info("create mapper factory with enabled cache");
      cache = CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1l, TimeUnit.HOURS).build();
    }
    else
    {
      logger.info("create mapper factory with disabled cache");
      cache = CacheBuilder.newBuilder().maximumSize(0).build();
    }
    this.converterFactory = converterFactory;
  }

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

  private <T> Mapper<T> getOrCreateMapperFromCache(final Class<T> type, final String parentDN) throws ExecutionException
  {
    return cache.get(new CacheKey(type, parentDN), new Callable<Mapper<T>>()
    {

      @Override
      public Mapper<T> call()
      {
        String name = type.getSimpleName().toLowerCase(Locale.ENGLISH).concat(".xml");
        Mapping mapping = BaseDirectory.getConfiguration("mapping/".concat(name), Mapping.class);
        if (mapping == null)
        {
          throw new MappingException("could not find mapping");
        }
        return new DefaultMapper<>(converterFactory, mapping, type, parentDN);
      }
    });
  }

  private static class CacheKey
  {

    private final Class<?> clazz;
    private final String parentDN;

    public CacheKey(Class<?> clazz, String parentDN)
    {
      this.clazz = clazz;
      this.parentDN = parentDN;
    }

    @Override
    public int hashCode()
    {
      return Objects.hashCode(clazz, parentDN);
    }

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

  }

}
