/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import com.google.inject.AbstractModule;

/**
 *
 * @author ssdorra
 */
public class MappingModule extends AbstractModule
{

  @Override
  protected void configure()
  {
    bind(MappingConverterFactory.class).to(InjectorMappingConverterFactory.class);
    bind(MapperFactory.class).to(DefaultMapperFactory.class);
  }

}
