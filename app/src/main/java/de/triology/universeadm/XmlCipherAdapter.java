package de.triology.universeadm;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
