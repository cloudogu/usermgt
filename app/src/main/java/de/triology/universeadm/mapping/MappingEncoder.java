package de.triology.universeadm.mapping;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public interface MappingEncoder
{

  public String encodeAsString(Object object);
  
  public String[] encodeAsMultiString(Object object);
  
  public byte[] encodeAsBytes(Object object);
  
  public byte[][] encodeAsMultiBytes(Object object);

}
