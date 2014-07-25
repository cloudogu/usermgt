/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.template;

import java.util.List;

/**
 * TODO use com.unboundid.asn1.ASN1OctetString
 *
 * @author ssdorra
 */
public interface MappingEncoder
{

  public String encodeAsString(Object object);
  
  public List<String> encodeAsMultiString(Object object);
  
  public byte[] encodeAsBytes(Object object);
  
  public List<byte[]> encodeAsMultiBytes(Object object);

}
