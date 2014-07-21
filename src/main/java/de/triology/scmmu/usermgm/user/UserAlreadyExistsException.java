/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package de.triology.scmmu.usermgm.user;

/**
 *
 * @author Sebastian Sdorra
 */
public class UserAlreadyExistsException extends UserException
{

  /** Field description */
  private static final long serialVersionUID = -1990930542567982382L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public UserAlreadyExistsException() {}

  /**
   * Constructs ...
   *
   *
   * @param message
   */
  public UserAlreadyExistsException(String message)
  {
    super(message);
  }

  /**
   * Constructs ...
   *
   *
   * @param cause
   */
  public UserAlreadyExistsException(Throwable cause)
  {
    super(cause);
  }

  /**
   * Constructs ...
   *
   *
   * @param message
   * @param cause
   */
  public UserAlreadyExistsException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
