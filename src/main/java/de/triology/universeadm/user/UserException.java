/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package de.triology.universeadm.user;

/**
 *
 * @author Sebastian Sdorra
 */
public class UserException extends RuntimeException
{

  /** Field description */
  private static final long serialVersionUID = 8505167324862759346L;

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public UserException() {}

  /**
   * Constructs ...
   *
   *
   * @param message
   */
  public UserException(String message)
  {
    super(message);
  }

  /**
   * Constructs ...
   *
   *
   * @param cause
   */
  public UserException(Throwable cause)
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
  public UserException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
