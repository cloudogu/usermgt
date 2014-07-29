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
public class EntityException extends RuntimeException
{

  public EntityException()
  {
  }

  public EntityException(String message)
  {
    super(message);
  }

  public EntityException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public EntityException(Throwable cause)
  {
    super(cause);
  }

}
