/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.unboundid.ldap.sdk.LDAPInterface;

/**
 *
 * @author Sebastian Sdorra
 */
public interface LDAPConnectionStrategy
{

  public void bind();

  public LDAPInterface get();

  public void release();
}
