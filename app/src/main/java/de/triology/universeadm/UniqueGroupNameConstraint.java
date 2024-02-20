package de.triology.universeadm;

import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.group.Group;

import java.util.List;

public class UniqueGroupNameConstraint extends Constraint<Group> {
  private final MappingHandler<Group> mapping;

  public UniqueGroupNameConstraint(final MappingHandler<Group> mapping) {
    this.mapping = mapping;
  }

  @Override
  public boolean violatedBy(final Group group, final Category currentCategory) {
    final List<Group> results = this.mapping.queryAll(group.getName());

    for (Group grp : results) {
      boolean isCreate = currentCategory == Category.CREATE;
      boolean groupNameExists = grp.getName().equals(group.getName());
      if (isCreate && groupNameExists){
          return true;
      }
    }

    return false;
  }

  @Override
  public ID getUniqueID() {
    return ID.UNIQUE_GROUP_NAME;
  }

}
