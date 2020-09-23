package de.triology.universeadm;

import de.triology.universeadm.mapping.MappingHandler;
import de.triology.universeadm.user.User;

import java.util.List;

public class UniqueMailConstraint extends Constraint<User> {
  private final MappingHandler<User> mapping;

  public UniqueMailConstraint(final MappingHandler<User> mapping) {
    this.mapping = mapping;
  }

  @Override
  public boolean violatedBy(final User user) {
    final List<User> results = this.mapping.search(user.getMail());

    for (User u : results) {
      if (u.getMail().equals(user.getMail())){
        return true;
      }
    }

    return false;
  }

  @Override
  public Type getType() {
    return Type.UNIQUE_EMAIL;
  }
}
