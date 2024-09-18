package de.triology.universeadm.mapping;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class IllegalQueryException extends RuntimeException
{

  /**
   * Constructs ...
   *
   */
  public IllegalQueryException() {}

  /**
   * Constructs ...
   *
   *
   * @param message
   */
  public IllegalQueryException(String message)
  {
    super(message);
  }

  /**
   * Constructs ...
   *
   *
   * @param cause
   */
  public IllegalQueryException(Throwable cause)
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
  public IllegalQueryException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
