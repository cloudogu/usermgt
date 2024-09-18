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
    this(converterFactory, new DefaultMappingProvider(), Stage.get());
  }

  /**
   * Constructs ...
   *
   *
   * @param converterFactory
   * @param mappingProvider
   * @param stage
   */
  @VisibleForTesting
  DefaultMapperFactory(MappingConverterFactory converterFactory,
    MappingProvider mappingProvider, Stage stage)
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

    this.mappingProvider = mappingProvider;
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
        Mapping mapping = mappingProvider.getMapping(type);

        if (mapping == null)
        {
          throw new MappingException("could not find mapping");
        }

        return new DefaultMapper<>(converterFactory, mapping, type, parentDN);
      }
    });
  }

  //~--- inner interfaces -----------------------------------------------------

  /**
   * Interface description
   *
   *
   * @version        Enter version here..., 14/08/27
   * @author         Enter your name here...
   */
  @VisibleForTesting
  static interface MappingProvider
  {

    /**
     * Method description
     *
     *
     * @param type
     *
     * @return
     */
    Mapping getMapping(Class<?> type);
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


  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/08/27
   * @author         Enter your name here...
   */
  private static class DefaultMappingProvider implements MappingProvider
  {

    /**
     * Method description
     *
     *
     * @param type
     *
     * @return
     */
    @Override
    public Mapping getMapping(Class<?> type)
    {
      String name =
        type.getSimpleName().toLowerCase(Locale.ENGLISH).concat(".xml");

      return BaseDirectory.getConfiguration("mapping/".concat(name),
        Mapping.class);
    }
  }


  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Cache<CacheKey, Mapper> cache;

  /** Field description */
  private final MappingConverterFactory converterFactory;

  /** Field description */
  private final MappingProvider mappingProvider;
}
