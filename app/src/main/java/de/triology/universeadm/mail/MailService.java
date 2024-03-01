package de.triology.universeadm.mail;

import de.triology.universeadm.user.User;
import jakarta.mail.NoSuchProviderException;

public interface MailService {
    void notify(User user);
}
