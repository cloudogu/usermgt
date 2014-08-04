/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.user;

import com.google.common.collect.Lists;

/**
 *
 * @author ssdorra
 */
public final class Users
{
  private Users(){}
  
  public static User createDent(){
    return new User(
      "dent", "Arthur Dent", "Arthur", "Dent", 
      "arthur.dent@hitchhiker.com", "hitchhiker123",
      Lists.newArrayList("Hitchhiker")
    );
  }
  
  public static User createTrillian(){
    return new User(
      "trillian", "Tricia McMillan", "Tricia", "McMillan", 
      "tricia.mcmillan@hitchhiker.com", "hitchhiker123",
      Lists.newArrayList("Hitchhiker")
    );
  }
}
