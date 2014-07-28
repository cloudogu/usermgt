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

  public String encodeAsString(Object object);
  
  public String[] encodeAsMultiString(Object object);
  
  public byte[] encodeAsBytes(Object object);
  
  public byte[][] encodeAsMultiBytes(Object object);

}
