/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author ssdorra
 */
public final class MappingAttributes
{

  private static final Cache<MappingAttribute, MappingEncoder> encoderCache = CacheBuilder.newBuilder().build();

  private static final Cache<MappingAttribute, MappingDecoder> decoderCache = CacheBuilder.newBuilder().build();

  private MappingAttributes()
  {
  }

  public static MappingDecoder getDecoder(MappingAttribute ma)
  {
    try
    {
      return getOrLoadDecoder(ma);
    }
    catch (ExecutionException ex)
    {
      throw new MappingException("could not get or load decoder", ex);
    }
  }

  public static MappingEncoder getEncoder(MappingAttribute ma)
  {
    try
    {
      return getOrLoadEncoder(ma);
    }
    catch (ExecutionException ex)
    {
      throw new MappingException("could not get or load encoder", ex);
    }
  }

  private static MappingDecoder getOrLoadDecoder(final MappingAttribute ma) throws ExecutionException
  {
    return decoderCache.get(ma, new Callable<MappingDecoder>()
    {

      @Override
      public MappingDecoder call() throws Exception
      {
        return ma.getDecoder().newInstance();
      }
    });
  }

  private static MappingEncoder getOrLoadEncoder(final MappingAttribute ma) throws ExecutionException
  {
    return encoderCache.get(ma, new Callable<MappingEncoder>()
    {

      @Override
      public MappingEncoder call() throws Exception
      {
        return ma.getEncoder().newInstance();
      }
    });
  }

  public static Object getObjectValue(MappingAttribute ma, Class<?> type, Attribute attribute)
  {
    Object value;
    String name = ma.getLdapName();

    if (ma.isBinary())
    {
      if (ma.isMultiValue())
      {
        value = getDecoder(ma).decodeFromMultiBytes(type, attribute.getValueByteArrays());
      }
      else
      {
        value = getDecoder(ma).decodeFromBytes(type, attribute.getValueByteArray());
      }
    }
    else
    {
      if (ma.isMultiValue())
      {
        value = getDecoder(ma).decodeFromMultiString(type, attribute.getValues());
      }
      else
      {
        value = getDecoder(ma).decodeFromString(type, attribute.getValue());
      }
    }
    return value;
  }

  public static Modification createModification(MappingAttribute ma, Object value)
  {
    Modification modification;
    String name = ma.getLdapName();

    if (ma.isBinary())
    {
      if (ma.isMultiValue())
      {
        byte[][] bytes = getEncoder(ma).encodeAsMultiBytes(value);
        modification = new Modification(ModificationType.REPLACE, name, bytes);
      }
      else
      {
        modification = new Modification(ModificationType.REPLACE, name, getEncoder(ma).encodeAsBytes(value));
      }
    }
    else
    {
      if (ma.isMultiValue())
      {
        String[] strings = getEncoder(ma).encodeAsMultiString(value);
        modification = new Modification(ModificationType.REPLACE, name, strings);
      }
      else
      {
        modification = new Modification(ModificationType.REPLACE, name, getEncoder(ma).encodeAsString(value));
      }
    }
    return modification;
  }

  public static Attribute createAttributeWithValue(MappingAttribute ma, Object value)
  {
    Attribute attribute;
    String name = ma.getLdapName();

    if (ma.isBinary())
    {
      if (ma.isMultiValue())
      {
        byte[][] bytes = getEncoder(ma).encodeAsMultiBytes(value);
        attribute = new Attribute(name, bytes);
      }
      else
      {
        attribute = new Attribute(name, getEncoder(ma).encodeAsBytes(value));
      }
    }
    else
    {
      if (ma.isMultiValue())
      {
        String[] strings = getEncoder(ma).encodeAsMultiString(value);
        attribute = new Attribute(name, strings);
      }
      else
      {
        attribute = new Attribute(name, getEncoder(ma).encodeAsString(value));
      }
    }
    return attribute;
  }

}
