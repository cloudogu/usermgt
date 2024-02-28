package de.triology.universeadm.mail;

import de.triology.universeadm.user.User;
import jakarta.mail.NoSuchProviderException;

public interface MailService {
    boolean notify(User user) throws MessageBuilderException, NoSuchProviderException;
}
