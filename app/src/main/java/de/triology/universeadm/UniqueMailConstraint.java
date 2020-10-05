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
    final List<User> results = this.mapping.search(user.getMail());

    // Check whether the email was changed in this request. This is done because it should not be possible
    // to change the email to an already existing email. But it should be possible if there are two users
    // with the same email, to change any other attribute of them.
    // This can occur when migrating from an old version (where duplicate mail addresses were possible) to this version.
    boolean hasChangedEmailInThisRequest = true;
    for (User u : results) {
      boolean isSameUser = u.getUsername().equals(user.getUsername());
      if (isSameUser){
        hasChangedEmailInThisRequest = !(u.getMail().equals(user.getMail()));
      }
    }

    for (User u : results) {
      boolean hasSameEMail = u.getMail().equals(user.getMail());
      boolean isSameUser = u.getUsername().equals(user.getUsername());

      switch (category){
        case CREATE:
          if (hasSameEMail){
            return true;
          }
        case MODIFY:
          if (!isSameUser && hasSameEMail && hasChangedEmailInThisRequest){
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
