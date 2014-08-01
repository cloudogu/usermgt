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
public interface MappingEncoder
{

  public <T> String encodeAsString(Mapper<T> mapper, Object object);
  
  public <T> String[] encodeAsMultiString(Mapper<T> mapper, Object object);
  
  public <T> byte[] encodeAsBytes(Mapper<T> mapper, Object object);
  
  public <T> byte[][] encodeAsMultiBytes(Mapper<T> mapper, Object object);

}
