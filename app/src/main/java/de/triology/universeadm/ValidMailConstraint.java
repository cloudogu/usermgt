package de.triology.universeadm;

import de.triology.universeadm.user.User;

import java.util.regex.Pattern;

public class ValidMailConstraint extends Constraint<User> {
    // simple email validation was chosen after discussing Internationalized domain name (öäü)
    final Pattern regex = Pattern.compile(".+@.+");

    @Override
    public boolean violatedBy(User user, Category category) {
        return ! this.regex.matcher(user.getMail()).find();
    }

    @Override
    public ID getUniqueID() {
        return ID.VALID_EMAIL;
    }
}
