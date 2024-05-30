package de.triology.universeadm;

import de.triology.universeadm.user.User;

import java.util.regex.Pattern;

public class ValidMailConstraint extends Constraint<User> {
    // from https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/email#validation
    final Pattern regex = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

    @Override
    public boolean violatedBy(User user, Category category) {
        return ! this.regex.matcher(user.getMail()).find();
    }

    @Override
    public ID getUniqueID() {
        return ID.VALID_EMAIL;
    }
}
