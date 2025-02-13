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

import de.triology.universeadm.*;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.user.imports.FieldConstraintViolationException;
import de.triology.universeadm.validation.Validator;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class LDAPUserManager extends AbstractLDAPManager<User>
    implements UserManager {

    /**
     * Field description
     */
    private static final String ATTRIBUTE_PASSWORD = "userPassword";

    /**
     * Field description
     */
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
     * @param strategy
     * @param configuration
     * @param hasher
     * @param mapperFactory
     * @param validator
     * @param eventBus
     * @param groupManager
     */
    @Inject
    public LDAPUserManager(LDAPConnectionStrategy strategy,
                           LDAPConfiguration configuration, LDAPHasher hasher,
                           MapperFactory mapperFactory, Validator validator, EventBus eventBus, GroupManager groupManager) {
        Mapper<User> mapper = mapperFactory.createMapper(User.class,
            configuration.getUserBaseDN());

        this.mapping = new UserMappingHandler(strategy, configuration, mapper,
            hasher, validator);
        this.eventBus = eventBus;

        this.constraints.add(new UniqueMailConstraint(this.mapping));
        this.constraints.add(new UniqueUsernameConstraint(this.mapping));
        this.constraints.add(new ValidMailConstraint());
        this.groupManager = groupManager;
    }

    //~--- methods --------------------------------------------------------------

    /**
     * Method description
     *
     * @param user
     */
    @Override
    public void create(User user) {
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
        Preconditions.checkNotNull(user, "user is required");
        checkConstraints(user, Constraint.Category.CREATE);

        mapping.create(user);
        eventBus.post(new UserEvent(user, EventType.CREATE));
        user.setPassword(DUMMY_PASSWORD);
    }

    public void createSynced(User user) {
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
        Preconditions.checkNotNull(user, "user is required");
        checkConstraints(user, Constraint.Category.CREATE);

        mapping.create(user);

        for (String groupName : user.getMemberOf()) {
            final Group group = groupManager.get(groupName);
            if (group != null) {
                if (!group.getMembers().contains(user.getUsername())) {
                    group.getMembers().add(user.getUsername());
                    groupManager.modify(group, false);
                } else {
                    logger.debug("group {} already contains member {}", groupName, user.getUsername());
                }
            } else {
                logger.warn("group {} does not exists", groupName);
            }
        }


        user.setPassword(DUMMY_PASSWORD);
    }

    /**
     * Checks if any constraints are violated.
     * If so, throws ConstraintViolationException containing all violations.
     *
     * @param user     - the user that is about to be checked for constraints
     * @param category - category contains the context of which constraints will be used
     * @throws FieldConstraintViolationException - if thrown this exception will contain the constraints that are violated
     *                                           in the given category.
     */
    void checkConstraints(final User user, final Constraint.Category category) {
        final List<Constraint.ID> violatedConstraints = new ArrayList<>();
        for (Constraint<User> constraint : this.constraints) {
            if (constraint.violatedBy(user, category)) {
                violatedConstraints.add(constraint.getUniqueID());
            }
        }
        if (!violatedConstraints.isEmpty()) {
            throw new FieldConstraintViolationException(violatedConstraints.toArray(new Constraint.ID[0]));
        }
    }

    /**
     * Method description
     *
     * @param user
     * @param fireEvent
     */
    @Override
    public void modify(User user, boolean fireEvent) {
        Preconditions.checkNotNull(user, "user is required");
        checkConstraints(user, Constraint.Category.MODIFY);

        Subject subject = SecurityUtils.getSubject();

        if (!subject.hasRole(Roles.ADMINISTRATOR)) {
            if (user.getUsername().equals(subject.getPrincipal().toString())) {
                User ldapUser = get(user.getUsername());

                if (!Iterables.elementsEqual(user.getMemberOf(),
                    ldapUser.getMemberOf())) {
                    throw new AuthorizationException(
                        "user has not enough privileges, to modify group membership");
                }
            } else {
                throw new AuthorizationException(
                    "user has not enough privileges, to modify other users");
            }
        }

        User oldUser = mapping.get(user.getUsername());

        mapping.modify(user);

        if (fireEvent) {

            // clone user ??
            eventBus.post(new UserEvent(user, oldUser));
        } else {
            logger.trace("events are disabled for this modification");
        }

        user.setPassword(DUMMY_PASSWORD);
    }

    /**
     * Remove user from ldap.
     *
     * @param user user to remove
     */
    @Override
    public void remove(User user) {
        Subject subject = SecurityUtils.getSubject();

        subject.checkRole(Roles.ADMINISTRATOR);
        Preconditions.checkNotNull(user, "user is required");

        Object principal = subject.getPrincipal();

        if (principal.equals(user.getUsername())) {
            //J-
            throw new UserSelfRemoveException(
                String.format("user %s has tried to remove himself", principal),
                principal
            );
            //J+
        }

        mapping.remove(user);
        eventBus.post(new UserEvent(user, EventType.REMOVE));
    }

    //~--- get methods ----------------------------------------------------------

    /**
     * Method description
     *
     * @param username
     * @return
     */
    @Override
    public User get(String username) {
        Preconditions.checkNotNull(username, "username is required");
        logger.debug("get user {}", username);

        Subject subject = SecurityUtils.getSubject();

        if (!subject.hasRole(Roles.ADMINISTRATOR)
            && !username.equals(subject.getPrincipal().toString())) {
            throw new AuthorizationException("user has not enough privileges");
        }

        return mapping.get(username);
    }

    @Override
    public PaginationResult<User> query(PaginationQuery query) {
        logger.debug("get paged users, query={} ", query);
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);

        return mapping.query(query);
    }

    //~--- inner classes --------------------------------------------------------

    /**
     * Class description
     *
     * @author Enter your name here...
     * @version Enter version here..., 14/08/20
     */
    private static class UserMappingHandler extends MappingHandler<User> {

        /**
         * Constructs ...
         *
         * @param strategy
         * @param configuration
         * @param mapper
         * @param hasher
         * @param validator
         */
        public UserMappingHandler(LDAPConnectionStrategy strategy,
                                  LDAPConfiguration configuration, Mapper<User> mapper, LDAPHasher hasher,
                                  Validator validator) {
            super(strategy, mapper, validator);
            this.configuration = configuration;
            this.hasher = hasher;
        }

        //~--- methods ------------------------------------------------------------

        /**
         * Method description
         *
         * @param entry
         * @param user
         * @return
         */
        @Override
        protected User consume(Entry entry, User user) {
            user.setPassword(DUMMY_PASSWORD);

            return user;
        }

        /**
         * Method description
         *
         * @param user
         * @param entry
         * @return
         */
        @Override
        protected Entry consume(User user, Entry entry) {
            String password = user.getPassword();

            if (!Strings.isNullOrEmpty(password)) {
                if (configuration.isRequirePreEncodedPasswords()) {
                    entry.setAttribute(ATTRIBUTE_PASSWORD, encodePassword(password));
                } else {
                    entry.setAttribute(ATTRIBUTE_PASSWORD, password);
                }
            }

            return entry;
        }

        /**
         * Method description
         *
         * @param user
         * @param mods
         * @return
         */
        @Override
        protected List<Modification> consume(User user, List<Modification> mods) {
            List<Modification> modifications = mods;
            String password = user.getPassword();

            if (!DUMMY_PASSWORD.equals(password)) {
                modifications = Lists.newArrayList(mods);

                if (configuration.isRequirePreEncodedPasswords()) {
                    modifications.add(new Modification(ModificationType.REPLACE,
                        ATTRIBUTE_PASSWORD, encodePassword(password)));
                } else {
                    modifications.add(new Modification(ModificationType.REPLACE,
                        ATTRIBUTE_PASSWORD, password));
                }
            }

            return modifications;
        }

        /**
         * Method description
         *
         * @param password
         * @return
         */
        private byte[] encodePassword(String password) {
            byte[] bytes = null;

            if (password != null) {
                bytes = hasher.hash(password);
            }

            return bytes;
        }

        //~--- fields -------------------------------------------------------------

        /**
         * Field description
         */
        private final LDAPConfiguration configuration;

        /**
         * Field description
         */
        private final LDAPHasher hasher;
    }


    //~--- fields ---------------------------------------------------------------

    /**
     * Field description
     */
    private final EventBus eventBus;
    private final GroupManager groupManager;

    /**
     * Field description
     */
    private final MappingHandler<User> mapping;

    @Override
    protected String typeToString(User e) {
        return e.getUsername();
    }
}
