package de.triology.universeadm.mapping;

import de.triology.universeadm.Unsupported;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public abstract class AbstractMappingDecoder implements MappingDecoder
{

  @Override
  public <T> Object decodeFromString(FieldDescriptor<T> type, String string)
  {
    throw Unsupported.unsupportedOperation();
  }

  @Override
  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings)
  {
    throw Unsupported.unsupportedOperation();
  }

  @Override
  public <T> Object decodeFromBytes(FieldDescriptor<T> type, byte[] bytes)
  {
    throw Unsupported.unsupportedOperation();
  }

  @Override
  public <T> Object decodeFromMultiBytes(FieldDescriptor<T> type, byte[][] bytes)
  {
    throw Unsupported.unsupportedOperation();
  }

}
