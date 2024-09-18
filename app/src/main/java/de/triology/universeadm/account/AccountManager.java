package de.triology.universeadm.account;

import de.triology.universeadm.user.User;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface AccountManager
{

  User getCurrentUser();

  void modifyCurrentUser(User account);

}
