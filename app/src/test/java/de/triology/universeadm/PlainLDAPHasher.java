package de.triology.universeadm;

import com.google.common.base.Charsets;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class PlainLDAPHasher extends LDAPHasher
{

  @Override
  public byte[] hash(String value)
  {
    return value.getBytes(Charsets.UTF_8);
  }

}
