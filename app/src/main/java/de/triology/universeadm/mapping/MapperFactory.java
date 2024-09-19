package de.triology.universeadm.mapping;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface MapperFactory
{
  
  public <T> Mapper<T> createMapper(Class<T> type, String parentDN);
  
}
