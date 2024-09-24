package de.triology.universeadm.mapping;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface MappingDecoder
{

  public <T> Object decodeFromString(FieldDescriptor<T> type, String string);

  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings);

  public <T> Object decodeFromBytes(FieldDescriptor<T> type, byte[] bytes);

  public <T> Object decodeFromMultiBytes(FieldDescriptor<T> type, byte[][] bytes);

}
