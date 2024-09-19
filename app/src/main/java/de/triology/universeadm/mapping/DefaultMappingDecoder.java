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
