/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;

/**
 *
 * @author Sebastian Sdorra
 */
@Singleton
public class DefaultLDAPConnectionStrategy implements LDAPConnectionStrategy
{

  private final ThreadLocal<LDAPConnection> store = new ThreadLocal<>();

  private final LDAPConfiguration configuration;

  @Inject
  public DefaultLDAPConnectionStrategy(LDAPConfiguration configuration)
  {
    this.configuration = configuration;
  }

  @Override
  public LDAPInterface get()
  {
    LDAPConnection connection = store.get();
    try
    {
      if (connection == null)
      {
        connection = new LDAPConnection(
          configuration.getHost(),
          configuration.getPort(), 
          configuration.getBindDN(), 
          configuration.getBindPassword()
        );
        store.set(connection);
      }
    } catch (LDAPException ex)
    {
      throw Throwables.propagate(ex);
    }
    return connection;
  }

  @Override
  public void bind()
  {
    // do nothing
  }

  @Override
  public void release()
  {
    LDAPConnection connection = store.get();
    if (connection != null)
    {
      connection.close();
      store.remove();
    }
  }

}
