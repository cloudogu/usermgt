package de.triology.universeadm.mapping;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.user.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class MemberMappingConverter extends AbstractMappingConverter
{
  
  private static final String DUMMY_DN = "cn=__dummy";
  
  private final LDAPConnectionStrategy strategy;
  private final Mapper<User> mapper;

  private static final Logger logger = LoggerFactory.getLogger(MemberMappingConverter.class);
  
  @Inject
  public MemberMappingConverter(LDAPConnectionStrategy strategy, LDAPConfiguration configuration, MapperFactory mapperFactory)
  {
    this.strategy = strategy;
    this.mapper = mapperFactory.createMapper(User.class, configuration.getUserBaseDN());
  }

  @Override
  public <T> Object decodeFromString(FieldDescriptor<T> type, String string)
  {
    return Decoders.decodeFromString(string);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings)
  { 
    Collection collection;
    if ( type.isSubClassOf(Collection.class) )
    {
      collection = Decoders.createCollection(type, strings.length);
      for ( String v : strings )
      {
        if (!DUMMY_DN.equals(v))
        {
          Object member = decodeFromString(null, v);
          if ( collection.contains(member) )
          {
            logger.warn("dublicate member {} found", member);
          }
          else 
          {
            collection.add(decodeFromString(null, v));
          }
        }
      }
      if ( type.isList() )
      {
        Collections.sort((List) collection);
      }
    } 
    else 
    {
      throw new MappingException("decoder supports only subtypes of collection");
    }
    return collection;
  }

  @Override
  public String encodeAsString(Object object)
  {
    return Encoders.encodeAsString(strategy, mapper, object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public String[] encodeAsMultiString(Object object)
  {
      List<String> dns =Encoders.encodeAsMultiStringList(strategy, mapper, object);
      dns.add(DUMMY_DN);
      return dns.toArray(new String[dns.size()]);
  
  }
  
  
}
