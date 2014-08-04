/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import de.triology.universeadm.AbstractLDAPManager;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.LDAPHasher;
import de.triology.universeadm.Roles;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.validation.Validator;
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
public class LDAPUserManager extends AbstractLDAPManager<User> implements UserManager
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
    Mapper<User> mapper = mapperFactory.createMapper(User.class, configuration.getUserBaseDN());
    this.mapping = new UserMappingHandler(strategy, configuration, mapper, hasher, validator);
    this.eventBus = eventBus;
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

    mapping.create(user);
    eventBus.post(new UserEvent(user, EventType.CREATE));
    user.setPassword(DUMMY_PASSWORD);
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
    Subject subject = SecurityUtils.getSubject();
    if (!subject.hasRole(Roles.ADMINISTRATOR))
    {
      if ( user.getUsername().equals(subject.getPrincipal().toString()) )
      {
        User ldapUser = get(user.getUsername());
        if ( ! Iterables.elementsEqual(user.getMemberOf(), ldapUser.getMemberOf()))
        {
          throw new AuthorizationException("user has not enough privileges, to modify group membership");
        }
      } 
      else 
      {
        throw new AuthorizationException("user has not enough privileges, to modify other users");
      }
    }

    User oldUser = mapping.get(user.getUsername());
    mapping.modify(user);
    eventBus.post(new UserEvent(user, oldUser));
    user.setPassword(DUMMY_PASSWORD);
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

    mapping.remove(user);
    eventBus.post(new UserEvent(user, EventType.REMOVE));
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
    if (!subject.hasRole(Roles.ADMINISTRATOR) && !username.equals(subject.getPrincipal().toString()))
    {
      throw new AuthorizationException("user has not enough privileges");
    }

    return mapping.get(username);
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

    return mapping.getAll();
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public List<User> search(String query)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    return mapping.search(query);
  }

  private static class UserMappingHandler extends MappingHandler<User>
  {

    private final LDAPHasher hasher;
    private final LDAPConfiguration configuration;

    public UserMappingHandler(LDAPConnectionStrategy strategy, LDAPConfiguration configuration, Mapper<User> mapper, LDAPHasher hasher, Validator validator)
    {
      super(strategy, mapper, validator);
      this.configuration = configuration;
      this.hasher = hasher;
    }

    @Override
    protected User consume(Entry entry, User user)
    {
      user.setPassword(DUMMY_PASSWORD);
      return user;
    }

    @Override
    protected Entry consume(User user, Entry entry)
    {
      String password = user.getPassword();
      if (!Strings.isNullOrEmpty(password))
      {
        if (configuration.isRequirePreEncodedPasswords())
        {
          entry.setAttribute(ATTRIBUTE_PASSWORD, encodePassword(password));
        }
        else
        {
          entry.setAttribute(ATTRIBUTE_PASSWORD, password);
        }
      }
      return entry;
    }

    @Override
    protected List<Modification> consume(User user, List<Modification> mods)
    {
      List<Modification> modifications = mods;
      String password = user.getPassword();
      if (!DUMMY_PASSWORD.equals(password))
      {
        modifications = Lists.newArrayList(mods);
        if (configuration.isRequirePreEncodedPasswords())
        {
          modifications.add(new Modification(ModificationType.REPLACE, ATTRIBUTE_PASSWORD, encodePassword(password)));
        }
        else
        {
          modifications.add(new Modification(ModificationType.REPLACE, ATTRIBUTE_PASSWORD, password));
        }
      }
      return modifications;
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

  }

  //~--- fields ---------------------------------------------------------------
  /**
   * Field description
   */
  private final MappingHandler<User> mapping;

  /**
   * Field description
   */
  private final EventBus eventBus;

}
