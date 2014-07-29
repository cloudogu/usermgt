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
public class EntityAlreadyExistsException extends EntityException
{

  public EntityAlreadyExistsException()
  {
  }

  public EntityAlreadyExistsException(String message)
  {
    super(message);
  }

  public EntityAlreadyExistsException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public EntityAlreadyExistsException(Throwable cause)
  {
    super(cause);
  }
  
}
