package de.triology.universeadm.mapping;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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

  @XmlElement(name = "base-filter")
  private String baseFilter;
  
  Mapping()
  {
  }
  
  public Mapping(List<String> objectClasses, List<MappingAttribute> attributes)
  {
    this(objectClasses, attributes, null);
  }
  
  public Mapping(List<String> objectClasses, List<MappingAttribute> attributes, String baseFilter)
  {
    this.objectClasses = objectClasses;
    this.attributes = attributes;
    this.baseFilter = baseFilter;
  }
  
  public List<MappingAttribute> getAttributes()
  {
    return attributes;
  }

  public List<String> getObjectClasses()
  {
    return objectClasses;
  }

  public String getBaseFilter()
  {
    return baseFilter;
  }
}
