package de.triology.universeadm;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
