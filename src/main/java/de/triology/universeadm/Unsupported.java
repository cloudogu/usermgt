/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

/**
 *
 * @author ssdorra
 */
public final class Unsupported
{
  
  private Unsupported(){}
  
  public static UnsupportedOperationException unsupportedOperation(){
    return new UnsupportedOperationException("Not supported yet.");
  }
  
}
