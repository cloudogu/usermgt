package de.triology.universeadm.user;

import de.triology.universeadm.Manager;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface UserManager extends Manager<User>
{
    public void createSynced(User user);
}
