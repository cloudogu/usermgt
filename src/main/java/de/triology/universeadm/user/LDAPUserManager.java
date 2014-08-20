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



package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import com.github.legman.EventBus;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class LDAPUserManager extends AbstractLDAPManager<User>
  implements UserManager
{

  /** Field description */
  private static final String ATTRIBUTE_PASSWORD = "userPassword";

  /** Field description */
  private static final String DUMMY_PASSWORD = "__dummypassword";

  /**
   * the logger for LDAPUserManager
   */
  private static final Logger logger =
    LoggerFactory.getLogger(LDAPUserManager.class);

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
    Mapper<User> mapper = mapperFactory.createMapper(User.class,
                            configuration.getUserBaseDN());

    this.mapping = new UserMappingHandler(strategy, configuration, mapper,
      hasher, validator);
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
   * @param fireEvent
   */
  @Override
  public void modify(User user, boolean fireEvent)
  {
    Preconditions.checkNotNull(user, "user is required");

    Subject subject = SecurityUtils.getSubject();

    if (!subject.hasRole(Roles.ADMINISTRATOR))
    {
      if (user.getUsername().equals(subject.getPrincipal().toString()))
      {
        User ldapUser = get(user.getUsername());

        if (!Iterables.elementsEqual(user.getMemberOf(),
          ldapUser.getMemberOf()))
        {
          throw new AuthorizationException(
            "user has not enough privileges, to modify group membership");
        }
      }
      else
      {
        throw new AuthorizationException(
          "user has not enough privileges, to modify other users");
      }
    }

    User oldUser = mapping.get(user.getUsername());

    mapping.modify(user);

    if (fireEvent)
    {

      // clone user ??
      eventBus.post(new UserEvent(user, oldUser));
    }
    else
    {
      logger.trace("events are disabled for this modification");
    }

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

  /**
   * Method description
   *
   *
   *
   * @param query
   * @return
   */
  @Override
  public List<User> search(String query)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);

    return mapping.search(query);
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

    if (!subject.hasRole(Roles.ADMINISTRATOR)
      &&!username.equals(subject.getPrincipal().toString()))
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

  //~--- inner classes --------------------------------------------------------

  /**
   * Class description
   *
   *
   * @version        Enter version here..., 14/08/20
   * @author         Enter your name here...    
   */
  private static class UserMappingHandler extends MappingHandler<User>
  {

    /**
     * Constructs ...
     *
     *
     * @param strategy
     * @param configuration
     * @param mapper
     * @param hasher
     * @param validator
     */
    public UserMappingHandler(LDAPConnectionStrategy strategy,
      LDAPConfiguration configuration, Mapper<User> mapper, LDAPHasher hasher,
      Validator validator)
    {
      super(strategy, mapper, validator);
      this.configuration = configuration;
      this.hasher = hasher;
    }

    //~--- methods ------------------------------------------------------------

    /**
     * Method description
     *
     *
     * @param entry
     * @param user
     *
     * @return
     */
    @Override
    protected User consume(Entry entry, User user)
    {
      user.setPassword(DUMMY_PASSWORD);

      return user;
    }

    /**
     * Method description
     *
     *
     * @param user
     * @param entry
     *
     * @return
     */
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

    /**
     * Method description
     *
     *
     * @param user
     * @param mods
     *
     * @return
     */
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
          modifications.add(new Modification(ModificationType.REPLACE,
            ATTRIBUTE_PASSWORD, encodePassword(password)));
        }
        else
        {
          modifications.add(new Modification(ModificationType.REPLACE,
            ATTRIBUTE_PASSWORD, password));
        }
      }

      return modifications;
    }

    /**
     * Method description
     *
     *
     * @param password
     *
     * @return
     */
    private byte[] encodePassword(String password)
    {
      byte[] bytes = null;

      if (password != null)
      {
        bytes = hasher.hash(password);
      }

      return bytes;
    }

    //~--- fields -------------------------------------------------------------

    /** Field description */
    private final LDAPConfiguration configuration;

    /** Field description */
    private final LDAPHasher hasher;
  }


  //~--- fields ---------------------------------------------------------------

  /**
   * Field description
   */
  private final EventBus eventBus;

  /**
   * Field description
   */
  private final MappingHandler<User> mapping;
}
