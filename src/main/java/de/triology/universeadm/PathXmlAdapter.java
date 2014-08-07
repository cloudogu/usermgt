/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import java.io.File;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author ssdorra
 */
public class PathXmlAdapter extends XmlAdapter<String, File>
{

  private static final String BASEDIR = "{basedir}";

  @Override
  public File unmarshal(String v)
  {
    return new File(v.replace(BASEDIR, BaseDirectory.get().getAbsolutePath()));
  }

  @Override
  public String marshal(File v)
  {
    return v.getAbsolutePath();
  }

}
