/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ssdorra
 */
@XmlRootElement(name = "template")
@XmlAccessorType(XmlAccessType.FIELD)
public class Mapping
{

  @XmlElement(name = "objectClass")
  @XmlElementWrapper(name = "objectClasses")
  private List<String> objectClasses;

  @XmlElement(name = "attribute")
  @XmlElementWrapper(name = "attributes")
  private List<MappingAttribute> attributes;

  Mapping()
  {
  }
  
  public Mapping(List<String> objectClasses, List<MappingAttribute> attributes)
  {
    this.objectClasses = objectClasses;
    this.attributes = attributes;
  }
  
  public List<MappingAttribute> getAttributes()
  {
    return attributes;
  }

  public List<String> getObjectClasses()
  {
    return objectClasses;
  }
}
