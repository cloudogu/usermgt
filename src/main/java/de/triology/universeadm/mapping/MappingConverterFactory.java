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
public interface MappingConverterFactory
{

  public MappingEncoder getEncoder(MappingAttribute attribute);

  public MappingDecoder getDecoder(MappingAttribute attribute);

}
