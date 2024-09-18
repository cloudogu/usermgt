package de.triology.universeadm.user;

/**
 * Exception is fired if a user tries to remove himself.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @since 1.1.0
 */
public class UserSelfRemoveException extends RuntimeException
{

  /**
   * Constructs ...
   *
   *
   * @param message
   * @param principal
   */
  public UserSelfRemoveException(String message, Object principal)
  {
    super(message);
    this.principal = principal;
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns principal which has tried to remove himself.
   *
   *
   * @return principal
   */
  public Object getPrincipal()
  {
    return principal;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final Object principal;
}
