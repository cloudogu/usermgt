package de.triology.universeadm;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @param <T>
 */
public interface EntityEvent<T>
{

  public EventType getType();

  public T getOldEntity();
  
  public T getEntity();
}
