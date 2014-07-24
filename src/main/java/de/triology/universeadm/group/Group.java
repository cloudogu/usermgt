/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.group;

import com.unboundid.ldap.sdk.persist.LDAPField;

/**
 *
 * @author ssdorra
 */
public class Group
{
  @LDAPField(attribute = "cn", inRDN = true)
  private String name;
}
