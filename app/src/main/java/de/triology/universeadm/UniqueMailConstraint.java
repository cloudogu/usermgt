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
  public boolean violatedBy(final User user, final Category category) {
    final List<User> results = this.mapping.queryAll(user.getMail());

    for (User u : results) {
      boolean hasSameEMail = u.getMail().equals(user.getMail());
      boolean isSameUser = u.getUsername().equals(user.getUsername());

      switch (category){
        case CREATE:
          if (hasSameEMail){
            return true;
          }
        case MODIFY:
          if (!isSameUser && hasSameEMail){
            return true;
          }
        default:
      }
    }

    return false;
  }

  @Override
  public ID getUniqueID() {
    return ID.UNIQUE_EMAIL;
  }
}
