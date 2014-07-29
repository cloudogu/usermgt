/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import de.triology.universeadm.EntityAlreadyExistsException;
import de.triology.universeadm.EntityException;
import de.triology.universeadm.EntityNotFoundException;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.LDAPHasher;
import de.triology.universeadm.LDAPUtil;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.Paginations;
import de.triology.universeadm.Roles;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.validation.Validator;
import java.util.Collections;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sebastian Sdorra
 */
public class LDAPUserManager implements UserManager
{

  private static final String DUMMY_PASSWORD = "__dummypassword";

  private static final String ATTRIBUTE_PASSWORD = "userPassword";

  /**
   * the logger for LDAPUserManager
   */
  private static final Logger logger
          = LoggerFactory.getLogger(LDAPUserManager.class);

  //~--- constructors ---------------------------------------------------------
  /**
   * Constructs ...
   *
   *
   * @param strategy
   * @param configuration
   * @param hasher
   * @param mapperFactory
   * @param validator
   * @param eventBus
   */
  @Inject
  public LDAPUserManager(LDAPConnectionStrategy strategy,
    LDAPConfiguration configuration, LDAPHasher hasher, 
    MapperFactory mapperFactory, Validator validator, EventBus eventBus)
  {
    this.strategy = strategy;
    this.configuration = configuration;
    this.hasher = hasher;
    this.mapper = mapperFactory.createMapper(User.class, configuration.getUserBaseDN());
    this.validator = validator;
    this.eventBus = eventBus;
    List<String> rattrs = this.mapper.getReturningAttributes();
    this.returningAttributes = rattrs.toArray(new String[rattrs.size()]);
  }
  
  //~--- methods --------------------------------------------------------------
  
  /**
   * Method description
   *
   *
   * @param user
   */
  @Override
  public void create(User user)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    Preconditions.checkNotNull(user, "user is required");
    // validate
    validator.validate(user, "user object is not valid");
    logger.info("create user {}", user.getUsername());

    try
    {
      Entry entry = mapper.convert(user);
      if (user.getPassword() != null)
      {
        entry.addAttribute(ATTRIBUTE_PASSWORD, encodePassword(user.getPassword()));
      }
      if (logger.isTraceEnabled())
      {
        logger.trace("create new user:\n{}", LDAPUtil.toLDIF(entry));
      }
      strategy.get().add(entry);
      eventBus.post(new UserEvent(user, EventType.CREATE));
      user.setPassword(DUMMY_PASSWORD);
    }
    catch (LDAPException ex)
    {
      if (ex.getResultCode() == ResultCode.ENTRY_ALREADY_EXISTS)
      {
        throw new EntityAlreadyExistsException(
                String.format("user %s already exists", user.getUsername()), ex);
      }
      else
      {
        throw new EntityException(
                "could not create user ".concat(user.getUsername()), ex);
      }
    }
  }

  /**
   * Method description
   *
   *
   * @param user
   */
  @Override
  public void modify(User user)
  {
    Preconditions.checkNotNull(user, "user is required");
    // validate
    validator.validate(user, "user object is not valid");
    logger.info("modify user {}", user.getUsername());
    
    Subject subject = SecurityUtils.getSubject();
    if (! subject.hasRole(Roles.ADMINISTRATOR) && ! user.getUsername().equals(subject.getPrincipal().toString()))
    {
      throw new AuthorizationException("user has not enough privileges");
    }

    try
    {
      List<Modification> modifications = Lists.newArrayList(mapper.getModifications(user));
      String password = user.getPassword();
      if (!DUMMY_PASSWORD.equals(password))
      {
        if (configuration.isRequirePreEncodedPasswords())
        {
          modifications.add(new Modification(ModificationType.REPLACE, ATTRIBUTE_PASSWORD, encodePassword(password)));
        }
        else
        {
          modifications.add(new Modification(ModificationType.REPLACE, ATTRIBUTE_PASSWORD, password));
        }
      }

      String dn = mapper.getDN(user.getUsername());

      if (logger.isTraceEnabled())
      {
        logger.trace("modify user:\n{}", LDAPUtil.toLDIF(dn, modifications));
      }

      strategy.get().modify(dn, modifications);
      eventBus.post(new UserEvent(user, EventType.MODIFY));
      user.setPassword(DUMMY_PASSWORD);
    }
    catch (LDAPException ex)
    {
      if ( ex.getResultCode() == ResultCode.NO_SUCH_OBJECT )
      {
        throw new EntityNotFoundException("could not find user ".concat(user.getUsername()));
      } 
      else 
      {
        throw new EntityException("could not modify user ".concat(user.getUsername()), ex);
      }
    }
  }

  private byte[] encodePassword(String password)
  {
    byte[] bytes = null;
    if (password != null)
    {
      bytes = hasher.hash(password);
    }
    return bytes;
  }

  /**
   * Method description
   *
   *
   * @param user
   */
  @Override
  public void remove(User user)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    Preconditions.checkNotNull(user, "user is required");
    logger.info("remove user {}", user.getUsername());

    try
    {
      strategy.get().delete(mapper.getDN(user.getUsername()));
      eventBus.post(new UserEvent(user, EventType.REMOVE));
    }
    catch (LDAPException ex)
    {
      throw new EntityException(
              "could not remove user ".concat(user.getUsername()), ex);
    }
  }

  //~--- get methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param username
   *
   * @return
   */
  @Override
  public User get(String username)
  {
    Preconditions.checkNotNull(username, "username is required");
    logger.debug("get user {}", username);
    
    Subject subject = SecurityUtils.getSubject();
    if (! subject.hasRole(Roles.ADMINISTRATOR) && ! username.equals(subject.getPrincipal().toString()))
    {
      throw new AuthorizationException("user has not enough privileges");
    }

    User user = null;

    try
    {
      
      Entry e = strategy.get().getEntry(mapper.getDN(username), returningAttributes);
      if (e != null)
      {
        user = mapper.convert(e);
        user.setPassword(DUMMY_PASSWORD);
      }
    }
    catch (LDAPException ex)
    {
      throw new EntityException("could not get user ".concat(username), ex);
    }

    return user;
  }

  @Override
  public PagedResultList<User> getAll(int start, int limit)
  {
    logger.debug("get paged users, start={} and limit={}", start, limit);
    return Paginations.createPaging(getAll(), start, limit);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public List<User> getAll()
  {
    logger.debug("get all users");
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);

    final List<User> users = Lists.newArrayList();

    try {
    SearchResult result = strategy.get().search(mapper.getParentDN(), SearchScope.SUB, mapper.getBaseFilter(), returningAttributes);
    for ( SearchResultEntry e : result.getSearchEntries() ){
      users.add(mapper.convert(e));
    }
    } catch (LDAPSearchException ex){
      throw new EntityException("could not get all users", ex);
    }
    
    Collections.sort(users);

    return ImmutableList.copyOf(users);
  }


  @Override
  public List<User> search(String query)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    String q = WILDCARD.concat(query).concat(WILDCARD);
    Filter base = mapper.getBaseFilter();

    List<User> users = Lists.newArrayList();
    try
    {
      List<Filter> or = Lists.newArrayList();
      for (String attribute : mapper.getSearchAttributes())
      {
        or.add(Filter.create(attribute.concat(EQUAL).concat(q)));
      }
      Filter filter = Filter.createANDFilter(base, Filter.createORFilter(or));
      logger.debug("start user search with filter {}", filter);

      SearchResult result = strategy.get().search(mapper.getParentDN(), SearchScope.SUB, filter, returningAttributes);
      for (SearchResultEntry e : result.getSearchEntries())
      {
        User user = mapper.convert(e);
        user.setPassword(DUMMY_PASSWORD);
        users.add(user);
      }
    }
    catch (LDAPException ex)
    {
      throw new EntityException("could not search users with query: ".concat(query), ex);
    }

    Collections.sort(users);

    return ImmutableList.copyOf(users);
  }

  private static final String WILDCARD = "*";

  private static final String EQUAL = "=";

  @Override
  public PagedResultList<User> search(String query, int start, int limit)
  {
    return Paginations.createPaging(search(query), start, limit);
  }

  //~--- fields ---------------------------------------------------------------
  /**
   * Field description
   */  
  private final String[] returningAttributes;
  
  /**
   * Field description
   */  
  private final Mapper<User> mapper;
  
  /**
   * Field description
   */  
  private final Validator validator;

  
  /**
   * Field description
   */
  private final LDAPHasher hasher;

  /**
   * Field description
   */
  private final LDAPConfiguration configuration;

  /**
   * Field description
   */
  private final EventBus eventBus;

  /**
   * Field description
   */
  private final LDAPConnectionStrategy strategy;
}
