/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm.validation;

/**
 *
 * @author ssdorra
 */
public interface Validator
{
  
  public <T> void validate(T object, String msg);
}
