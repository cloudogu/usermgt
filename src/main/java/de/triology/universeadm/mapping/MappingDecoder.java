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

  public <T> Object decodeFromString(Mapper<T> mapper, FieldDescriptor<T> type, String string);

  public <T> Object decodeFromMultiString(Mapper<T> mapper, FieldDescriptor<T> type, String[] strings);

  public <T> Object decodeFromBytes(Mapper<T> mapper, FieldDescriptor<T> type, byte[] bytes);

  public <T> Object decodeFromMultiBytes(Mapper<T> mapper, FieldDescriptor<T> type, byte[][] bytes);

}
