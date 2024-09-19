package de.triology.universeadm.mapping;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface MappingConverterFactory
{

  public MappingEncoder getEncoder(MappingAttribute attribute);

  public MappingDecoder getDecoder(MappingAttribute attribute);

}
