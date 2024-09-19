package de.triology.universeadm.group;

import com.github.legman.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.*;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.user.User;
import de.triology.universeadm.validation.Validator;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class LDAPGroupManager extends AbstractLDAPManager<Group> implements GroupManager {

    private static final Logger logger = LoggerFactory.getLogger(LDAPGroupManager.class);


    private final MappingHandler<Group> mapping;
    private final EventBus eventBus;
    private final UndeletableGroupManager undeletableGroupManager;

    @Inject
    public LDAPGroupManager(LDAPConnectionStrategy strategy,
                            LDAPConfiguration configuration,
                            UndeletableGroupManager undeletableGroupManager,
                            MapperFactory mapperFactory, Validator validator, EventBus eventBus) {
        this.undeletableGroupManager = undeletableGroupManager;
        Mapper<Group> mapper = mapperFactory.createMapper(Group.class, configuration.getGroupBaseDN());
        this.mapping = new MappingHandler<>(strategy, mapper, validator);
        this.eventBus = eventBus;
        this.constraints.add(new UniqueGroupNameConstraint(this.mapping));
    }

    @Override
    public void create(Group group) {
        logger.debug("create group {}", group.getName());
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
        checkConstraints(group, Constraint.Category.CREATE);

        mapping.create(group);
        eventBus.post(new GroupEvent(group, EventType.CREATE));
    }

    /**
     * Checks if any constraints are violated.
     * If so, throws ConstraintViolationException containing all violations.
     * @param group
     * @param category
     * @throws UniqueConstraintViolationException
     */
    private void checkConstraints(final Group group, final Constraint.Category category) {
        final List<Constraint.ID> violatedConstraints = new ArrayList<>();
        for (Constraint<Group> constraint : this.constraints) {
            if (constraint.violatedBy(group, category)) {
                violatedConstraints.add(constraint.getUniqueID());
            }
        }
        if (!violatedConstraints.isEmpty()) {
            throw new UniqueConstraintViolationException(violatedConstraints.toArray(new Constraint.ID[2]));
        }
    }

    @Override
    public void modify(Group group, boolean fireEvent) {
        logger.debug("modify group {}", group.getName());
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
        Group oldGroup = mapping.get(group.getName());
        mapping.modify(group);
        if (fireEvent) {
            eventBus.post(new GroupEvent(group, oldGroup));
        } else {
            logger.trace("events are disabled for this modification");
        }
    }

    @Override
    public void remove(Group group) {
        logger.debug("remove group {}", group.getName());
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
        if (undeletableGroupManager.isGroupUndeletable(group.getName())) {
            throw new CannotRemoveException();
        }
        mapping.remove(group);
        eventBus.post(new GroupEvent(group, EventType.REMOVE));
    }

    @Override
    public Group get(String name) {
        logger.debug("get group {}", name);
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
        return mapping.get(name);
    }

    @Override
    public PaginationResult<Group> query(PaginationQuery query) {
      logger.debug("get paged groups, query={} ", query);
        SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);

        return mapping.query(query);
    }

    @Override
    protected String typeToString(Group e) {
        return e.getName();
    }
}
