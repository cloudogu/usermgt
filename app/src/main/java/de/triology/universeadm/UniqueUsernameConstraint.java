package de.triology.universeadm;

import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.user.User;

import java.util.List;

public class UniqueUsernameConstraint extends Constraint<User> {
  private final MappingHandler<User> mapping;

  public UniqueUsernameConstraint(final MappingHandler<User> mapping) {
    this.mapping = mapping;
  }

  @Override
  public boolean violatedBy(final User user, final Category currentCategory) {
    final List<User> results = this.mapping.queryAll(user.getUsername());

    for (User u : results) {
      boolean isCreate = currentCategory == Category.CREATE;
      boolean usernameExists = u.getUsername().equals(user.getUsername());
      if (isCreate && usernameExists){
          return true;
      }
    }

    return false;
  }

  @Override
  public ID getUniqueID() {
    return ID.UNIQUE_USERNAME;
  }

}
