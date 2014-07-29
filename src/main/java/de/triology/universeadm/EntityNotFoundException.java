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
public class EntityNotFoundException extends EntityException
{

  public EntityNotFoundException()
  {
  }

  public EntityNotFoundException(String message)
  {
    super(message);
  }

  public EntityNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public EntityNotFoundException(Throwable cause)
  {
    super(cause);
  }

}
