/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.group;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.AbstractLDAPManager;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.Roles;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.validation.Validator;
import java.util.List;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public class LDAPGroupManager extends AbstractLDAPManager<Group> implements GroupManager
{

  private static final Logger logger = LoggerFactory.getLogger(LDAPGroupManager.class);
  

  private final MappingHandler<Group> mapping;
  private final EventBus eventBus;
  
  @Inject
  public LDAPGroupManager(LDAPConnectionStrategy strategy,
                         LDAPConfiguration configuration,
                         MapperFactory mapperFactory, Validator validator, EventBus eventBus)
  {
    Mapper<Group> mapper = mapperFactory.createMapper(Group.class, configuration.getGroupBaseDN());
    this.mapping = new MappingHandler<>(strategy, mapper, validator);
    this.eventBus = eventBus;
  }
  
  @Override
  public void create(Group group)
  {
    logger.debug("create group {}", group.getName());
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    mapping.create(group);
    eventBus.post(new GroupEvent(group, EventType.CREATE));
  }

  @Override
  public void modify(Group group)
  {
    logger.debug("modify group {}", group.getName());
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    mapping.modify(group);
    eventBus.post(new GroupEvent(group, EventType.MODIFY));
  }

  @Override
  public void remove(Group group)
  {
    logger.debug("remove group {}", group.getName());
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    mapping.remove(group);
    eventBus.post(new GroupEvent(group, EventType.REMOVE));
  }

  @Override
  public Group get(String name)
  {
    logger.debug("get group {}", name);
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    return mapping.get(name);
  }

  @Override
  public List<Group> getAll()
  {
    logger.debug("get all groups");
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);

    return mapping.getAll();
  }

  @Override
  public List<Group> search(String query)
  {
    logger.debug("search groups with query {}", query);
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);

    return mapping.search(query);
  }
  
}
