/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import com.google.common.base.Charsets;

/**
 *
 * @author ssdorra
 */
public class PlainLDAPHasher extends LDAPHasher
{

  @Override
  public byte[] hash(String value)
  {
    return value.getBytes(Charsets.UTF_8);
  }

}
