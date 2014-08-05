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
public abstract class AbstractMappingEncoder implements MappingEncoder
{

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
