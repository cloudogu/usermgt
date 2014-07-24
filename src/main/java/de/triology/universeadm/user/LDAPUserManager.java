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
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.persist.LDAPObjectHandler;
import com.unboundid.ldap.sdk.persist.LDAPPersistException;
import com.unboundid.ldap.sdk.persist.LDAPPersister;
import com.unboundid.ldap.sdk.persist.ObjectSearchListener;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.LDAPHasher;
import de.triology.universeadm.LDAPUtil;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.Paginations;
import de.triology.universeadm.validation.Validator;
import java.util.Collections;
import java.util.List;
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
   * @param validator
   * @param eventBus
   */
  @Inject
  public LDAPUserManager(LDAPConnectionStrategy strategy,
          LDAPConfiguration configuration, LDAPHasher hasher, Validator validator, EventBus eventBus)
  {
    this.strategy = strategy;
    this.configuration = configuration;
    this.hasher = hasher;
    this.validator = validator;
    this.eventBus = eventBus;

    try
    {
      this.persister = LDAPPersister.getInstance(User.class);
    }
    catch (LDAPPersistException ex)
    {
      throw new UserException("could not create ldap persister", ex);
    }
  }
  
  private final Validator validator;

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
    Preconditions.checkNotNull(user, "user is required");
    // validate
    validator.validate(user, "user object is not valid");
    logger.info("create user {}", user.getUsername());

    try
    {
      Entry entry = persister.encode(user, configuration.getUserBaseDN());
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
        throw new UserAlreadyExistsException(
                String.format("user %s already exists", user.getUsername()), ex);
      }
      else
      {
        throw new UserException(
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

    try
    {
      List<Modification> modifications = Lists.newArrayList(persister.getModifications(user, true));
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

      String dn = createDN(user);

      if (logger.isTraceEnabled())
      {
        logger.trace("modify user:\n{}", LDAPUtil.toLDIF(dn, modifications));
      }

      strategy.get().modify(createDN(user), modifications);
      eventBus.post(new UserEvent(user, EventType.MODIFY));
      user.setPassword(DUMMY_PASSWORD);
    }
    catch (LDAPException ex)
    {
      throw new UserException(
              "could not modify user ".concat(user.getUsername()), ex);
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
    Preconditions.checkNotNull(user, "user is required");
    logger.info("remove user {}", user.getUsername());

    try
    {
      strategy.get().delete(createDN(user));
      eventBus.post(new UserEvent(user, EventType.REMOVE));
    }
    catch (LDAPException ex)
    {
      throw new UserException(
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

    User user;

    try
    {
      user = persister.get(createDN(username), strategy.get());
      if (user != null)
      {
        user.setPassword(DUMMY_PASSWORD);
      }
    }
    catch (LDAPPersistException ex)
    {
      throw new UserException("could not get user ".concat(username), ex);
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

    final List<User> users = Lists.newArrayList();

    try
    {
      persister.getAll(strategy.get(), configuration.getUserBaseDN(),
              new ObjectSearchListener<User>()
              {

                @Override
                public void objectReturned(User o)
                {
                  o.setPassword(DUMMY_PASSWORD);
                  users.add(o);
                }

                @Override
                public void unparsableEntryReturned(SearchResultEntry entry,
                        LDAPPersistException exception)
                {
                  logger.warn("could not parse entry ".concat(entry.getDN()),
                          exception);
                }

                @Override
                public void searchReferenceReturned(
                        SearchResultReference searchReference)
                {

                        }
              });
    }
    catch (LDAPPersistException ex)
    {
      throw new UserException("could not get all users", ex);
    }

    Collections.sort(users);

    return ImmutableList.copyOf(users);
  }

  //~--- methods --------------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param username
   *
   * @return
   *
   * @throws LDAPPersistException
   */
  private String createDN(String username) throws LDAPPersistException
  {
    return createDN(new User(username));
  }

  /**
   * Method description
   *
   *
   * @param user
   *
   * @return
   *
   * @throws LDAPPersistException
   */
  private String createDN(User user) throws LDAPPersistException
  {
    return persister.getObjectHandler().constructDN(user,
            configuration.getUserBaseDN());
  }

  @Override
  public List<User> search(String query)
  {
    String q = WILDCARD.concat(query).concat(WILDCARD);
    Filter base = persister.getObjectHandler().createBaseFilter();
    LDAPObjectHandler<User> oh = persister.getObjectHandler();

    List<User> users = Lists.newArrayList();
    try
    {
      List<Filter> or = Lists.newArrayList();
      for (String attribute : oh.getAttributesToRequest())
      {
        or.add(Filter.create(attribute.concat(EQUAL).concat(q)));
      }
      Filter filter = Filter.createANDFilter(base, Filter.createORFilter(or));
      logger.debug("start user search with filter {}", filter);

      SearchResult result = strategy.get().search(configuration.getUserBaseDN(), SearchScope.SUB, filter, oh.getAttributesToRequest());
      for (SearchResultEntry e : result.getSearchEntries())
      {
        try
        {
          User user = persister.decode(e);
          user.setPassword(DUMMY_PASSWORD);
          users.add(user);
        }
        catch (LDAPPersistException ex)
        {
          logger.error("could not decode user ".concat(e.getDN()), ex);
        }
      }
    }
    catch (LDAPException ex)
    {
      throw new UserException("could not search users with query: ".concat(query), ex);
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
  private final LDAPPersister<User> persister;

  /**
   * Field description
   */
  private final LDAPConnectionStrategy strategy;
}
