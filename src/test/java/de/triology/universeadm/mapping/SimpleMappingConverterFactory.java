/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

/**
 *
 * @author ssdorra
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
