package de.triology.universeadm.mapping;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class SimpleMappingConverterFactory implements MappingConverterFactory
{

  @Override
  public MappingEncoder getEncoder(MappingAttribute attribute)
  {
    try 
    {
      return attribute.getEncoder().newInstance();
    }
    catch (IllegalAccessException | InstantiationException ex)
    {
      throw new MappingException("could not create encoder", ex);
    }
  }

  @Override
  public MappingDecoder getDecoder(MappingAttribute attribute)
  {
    try 
    {
      return attribute.getDecoder().newInstance();
    }
    catch (IllegalAccessException | InstantiationException ex)
    {
      throw new MappingException("could not create decoder", ex);
    }
  }
  
}
