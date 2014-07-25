/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.template;

/**
 *
 * @author ssdorra
 */
public class MappingException extends RuntimeException
{

  public MappingException()
  {
  }

  public MappingException(String message)
  {
    super(message);
  }

  public MappingException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public MappingException(Throwable cause)
  {
    super(cause);
  }
  
}
