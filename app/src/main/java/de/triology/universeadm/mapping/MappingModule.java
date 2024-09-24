package de.triology.universeadm.mapping;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.AbstractModule;
import com.google.inject.Module;

import org.kohsuke.MetaInfServices;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@MetaInfServices(Module.class)
public class MappingModule extends AbstractModule
{

  /**
   * Method description
   *
   */
  @Override
  protected void configure()
  {
    bind(MappingConverterFactory.class).to(
      InjectorMappingConverterFactory.class);
    bind(MapperFactory.class).to(DefaultMapperFactory.class);

    // rest
    bind(IllegalQueryExceptionMapper.class);
  }
}
