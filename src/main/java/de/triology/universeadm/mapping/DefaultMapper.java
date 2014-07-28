/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public class DefaultMapper<T> implements Mapper<T>
{  

  private final Mapping mapping;
  private final String parentDN;
  private final ClassDescriptor<T> type;
  private final List<String> returningAttributes;
  private final Attribute objectClasses;
  private final MappingAttribute rdn;
  private final Filter baseFilter;
  private final List<String> searchAttributes;

  public DefaultMapper(Mapping mapping, Class<T> type, String parentDN)
  {
    Preconditions.checkNotNull(mapping, "mapping is required");
    Preconditions.checkNotNull(parentDN, "parentDN is required");
    
    this.mapping = mapping;
    this.parentDN = parentDN;
    this.type = new ClassDescriptor<>(type);
    this.returningAttributes = Lists.transform(mapping.getAttributes(), toLdapName);
    this.searchAttributes = FluentIterable.from(mapping.getAttributes()).filter(searchPredicate).transform(toLdapName).toList();
    this.objectClasses = new Attribute("objectClass", mapping.getObjectClasses());
    Iterable<MappingAttribute> rdns = Iterables.filter(mapping.getAttributes(), rdnPredicate);
    this.rdn = Iterables.getOnlyElement(rdns);
    try 
    {
      this.baseFilter = Filter.create("(".concat(rdn.getLdapName()).concat("=*)"));
    } 
    catch (LDAPException ex)
    {
      throw new MappingException("could not create base filter", ex);
    }
  }
  
  @Override
  public String getParentDN()
  {
    return parentDN;
  }

  @Override
  public Filter getBaseFilter()
  {
    return baseFilter;
  }

  @Override
  public List<String> getSearchAttributes()
  {
    return searchAttributes;
  }

  @Override
  public String getDN(String rdn)
  {
    StringBuilder dn = new StringBuilder(this.rdn.getLdapName());
    return dn.append("=").append(rdn).append(",").append(parentDN).toString();
  }

  @Override
  public List<String> getReturningAttributes()
  {
    return returningAttributes;
  }

  @Override
  public Entry convert(T object)
  {
    List<Attribute> attributes = Lists.newArrayList();
    attributes.add(objectClasses);

    String rdn = null;

    for (MappingAttribute ma : filter(mapping.getAttributes(), createPredicate))
    {
      Attribute attribute = createAttribute(ma, object);
      if (attribute != null)
      {
        if (ma.isRdn())
        {
          rdn = ma.getLdapName().concat("=").concat(attribute.getValue());
        }

        attributes.add(attribute);
      }
    }

    if (rdn == null)
    {
      throw new MappingException("could not find rdn for entry");
    }
    return new Entry(rdn.concat(",").concat(parentDN), attributes);
  }

  private Attribute createAttribute(MappingAttribute ma, T object)
  {
    Attribute attribute = null;
    Object value = getObjectValue(object, ma);
    if (value != null)
    {
      attribute = MappingAttributes.createAttributeWithValue(ma, value);
    }

    return attribute;
  }

  private Object getObjectValue(Object object, MappingAttribute ma)
  {
    Object value = null;
    try
    {
      value = PropertyUtils.getProperty(object, ma.getName());
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
    {
      throw new MappingException("could not convert object to entry", ex);
    }
    return value;
  }

  @Override
  public T convert(Entry entry)
  {
    T object = type.newInstance();
    for (MappingAttribute ma : filter(mapping.getAttributes(), readPredicate))
    {
      FieldDescriptor<T> desc = type.getField(ma.getName());
      if (desc == null)
      {
        throw new MappingException("could not find type token for field");
      }
      Attribute attribute = entry.getAttribute(ma.getLdapName());
      if (attribute != null)
      {
        Object value = MappingAttributes.getObjectValue(ma, desc, attribute);
        setObjectValue(object, ma, value);
      }
    }
    return object;
  }

  private void setObjectValue(Object object, MappingAttribute ma, Object value)
  {
    try
    {
      PropertyUtils.setProperty(object, ma.getName(), value);
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex)
    {
      throw new MappingException("could not set object value", ex);
    }
  }

  private Iterable<MappingAttribute> filter(Iterable<MappingAttribute> iterable, Predicate<MappingAttribute> predicate)
  {
    return Iterables.filter(iterable, predicate);
  }

  @Override
  public List<Modification> getModifications(T object)
  {
    List<Modification> modifications = Lists.newArrayList();
    for (MappingAttribute ma : filter(mapping.getAttributes(), modifyPredicate))
    {
      Object value = getObjectValue(object, ma);
      Modification modification = MappingAttributes.createModification(ma, value);
      if (modification != null)
      {
        modifications.add(modification);
      }
    }
    return modifications;
  }

  // private classes
  private static final Function<MappingAttribute, String> toLdapName = new Function<MappingAttribute, String>()
  {

    @Override
    public String apply(MappingAttribute input)
    {
      return input.getLdapName();
    }
  };

  private static final Predicate<MappingAttribute> rdnPredicate = new Predicate<MappingAttribute>()
  {

    @Override
    public boolean apply(MappingAttribute input)
    {
      return input.isRdn();
    }
  };
  
  private static final Predicate<MappingAttribute> createPredicate = new Predicate<MappingAttribute>()
  {

    @Override
    public boolean apply(MappingAttribute input)
    {
      return input.isInCreate();
    }
  };

  private static final Predicate<MappingAttribute> modifyPredicate = new Predicate<MappingAttribute>()
  {

    @Override
    public boolean apply(MappingAttribute input)
    {
      return input.isInModify();
    }
  };

  private static final Predicate<MappingAttribute> readPredicate = new Predicate<MappingAttribute>()
  {

    @Override
    public boolean apply(MappingAttribute input)
    {
      return input.isInRead();
    }
  };
  
  private static final Predicate<MappingAttribute> searchPredicate = new Predicate<MappingAttribute>()
  {

    @Override
    public boolean apply(MappingAttribute input)
    {
      return input.isInSearch();
    }
  };

}
