/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ssdorra
 */
public class TrimXmlAdapter extends XmlAdapter<String, String>
{

  @Override
  public String unmarshal(String v) throws Exception
  {
    return v != null ? v.trim() : v;
  }

  @Override
  public String marshal(String v) throws Exception
  {
    return v;
  }
  
}
