package de.triology.universeadm.validation;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface Validator
{
  
  public <T> void validate(T object, String msg);
}
