package de.triology.universeadm.mapping;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class TrimXmlAdapter extends XmlAdapter<String, String>
{

  @Override
  public String unmarshal(String v)
  {
    return v != null ? v.trim() : v;
  }

  @Override
  public String marshal(String v)
  {
    return v;
  }
  
}
