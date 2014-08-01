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
public abstract class AbstractMappingDecoder implements MappingDecoder
{

  @Override
  public <T> Object decodeFromString(FieldDescriptor<T> type, String string)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <T> Object decodeFromMultiString(FieldDescriptor<T> type, String[] strings)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <T> Object decodeFromBytes(FieldDescriptor<T> type, byte[] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public <T> Object decodeFromMultiBytes(FieldDescriptor<T> type, byte[][] bytes)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
