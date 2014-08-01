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
public interface MappingDecoder
{

  public <T> Object decodeFromString(FieldDescriptor<T> type, String string);

  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings);

  public <T> Object decodeFromBytes(FieldDescriptor<T> type, byte[] bytes);

  public <T> Object decodeFromMultiBytes(FieldDescriptor<T> type, byte[][] bytes);

}
