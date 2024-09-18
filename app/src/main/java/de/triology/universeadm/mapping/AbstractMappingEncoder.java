package de.triology.universeadm.mapping;

import de.triology.universeadm.Unsupported;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public abstract class AbstractMappingEncoder implements MappingEncoder
{

  @Override
  public String encodeAsString(Object object)
  {
    throw Unsupported.unsupportedOperation();
  }

  @Override
  public String[] encodeAsMultiString(Object object)
  {
    throw Unsupported.unsupportedOperation();
  }

  @Override
  public byte[] encodeAsBytes(Object object)
  {
    throw Unsupported.unsupportedOperation();
  }

  @Override
  public byte[][] encodeAsMultiBytes(Object object)
  {
    throw Unsupported.unsupportedOperation();
  }

}
