/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

/**
 *
 * @author ssdorra
 */
public class UserNotFoundException extends UserException
{

  public UserNotFoundException()
  {
  }

  public UserNotFoundException(String message)
  {
    super(message);
  }

  public UserNotFoundException(Throwable cause)
  {
    super(cause);
  }

  public UserNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
