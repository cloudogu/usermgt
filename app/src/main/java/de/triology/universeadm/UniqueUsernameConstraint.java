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
  public boolean violatedBy(final User user) {
    final List<User> results = this.mapping.search(user.getUsername());

    for (User u : results) {
      if (u.getUsername().equals(user.getUsername())) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Type getType() {
    return Type.UNIQUE_USERNAME;
  }

}
