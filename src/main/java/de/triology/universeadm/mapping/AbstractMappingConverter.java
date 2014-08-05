/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import de.triology.universeadm.Unsupported;

/**
 *
 * @author ssdorra
 */
public abstract class AbstractMappingConverter implements MappingDecoder, MappingEncoder
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
