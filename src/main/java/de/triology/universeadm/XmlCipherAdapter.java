/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ssdorra
 */
public class XmlCipherAdapter extends XmlAdapter<String, String>
{

  @Override
  public String unmarshal(String v)
  {
    return Cipher.decrypt(v);
  }

  @Override
  public String marshal(String v)
  {
    return Cipher.encrypt(v);
  }

}
