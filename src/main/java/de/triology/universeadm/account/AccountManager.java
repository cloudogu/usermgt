/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.account;

import de.triology.universeadm.user.User;

/**
 *
 * @author ssdorra
 */
public interface AccountManager
{

  public User getCurrentUser();

  public void modifyCurrentUser(User account);

}
