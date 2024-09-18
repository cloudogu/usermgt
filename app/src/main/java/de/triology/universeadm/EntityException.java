package de.triology.universeadm;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
