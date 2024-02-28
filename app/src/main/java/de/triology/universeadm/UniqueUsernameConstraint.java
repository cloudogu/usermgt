package de.triology.universeadm;

import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.user.User;

public class UniqueUsernameConstraint extends Constraint<User> {
  private final MappingHandler<User> mapping;

  public UniqueUsernameConstraint(final MappingHandler<User> mapping) {
    this.mapping = mapping;
  }

  @Override
  public boolean violatedBy(final User user, final Category currentCategory) {
      if (currentCategory != Category.CREATE) {
          return false;
      }

      final User existingUser = this.mapping.get(user.getUsername());

      return existingUser != null;
  }

  @Override
  public ID getUniqueID() {
    return ID.UNIQUE_USERNAME;
  }

}
