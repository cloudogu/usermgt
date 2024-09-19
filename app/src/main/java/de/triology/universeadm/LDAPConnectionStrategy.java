package de.triology.universeadm;

import com.unboundid.ldap.sdk.LDAPInterface;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface LDAPConnectionStrategy
{

  public void bind();

  public LDAPInterface get();

  public void release();
}
