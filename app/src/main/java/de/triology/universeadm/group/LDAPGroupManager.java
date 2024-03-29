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
