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
public interface MappingDecoder
{
  
  public Object decodeFromString(Class<?> type, String string);
  
  public Object decodeFromMultiString(Class<?> type, List<String> strings);
  
  public Object decodeFromBytes(Class<?> type, byte[] bytes);
  
  public Object decodeFromMultiBytes(Class<?> type, List<byte[]> bytes);
  
}
