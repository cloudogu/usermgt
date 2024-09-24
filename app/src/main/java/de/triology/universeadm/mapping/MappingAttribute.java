package de.triology.universeadm.mapping;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@XmlRootElement(name = "attribute")
public class MappingAttribute
{

  public static class MappingAttributeBuilder
  {

    private final String name;
    private String ldapName;
    private final List<String> siblings = Lists.newArrayList();
    private boolean inRead = true;
    private boolean inModify = true;
    private boolean inCreate = true;
    private boolean inSearch = true;
    private boolean binary = false;
    private boolean multiValue = false;
    private boolean rdn = false;
    private Class<? extends MappingEncoder> encoder = DefaultMappingEncoder.class;
    private Class<? extends MappingDecoder> decoder = DefaultMappingDecoder.class;

    public MappingAttributeBuilder(String name)
    {
      this.name = name;
    }

    public MappingAttributeBuilder ldapName(String ldapName)
    {
      this.ldapName = ldapName;
      return this;
    }
    
    public MappingAttributeBuilder sibling(String sibling){
      this.siblings.add(sibling);
      return this;
    }

    public MappingAttributeBuilder inRead(boolean inRead)
    {
      this.inRead = inRead;
      return this;
    }

    public MappingAttributeBuilder inModify(boolean inModify)
    {
      this.inModify = inModify;
      return this;
    }

    public MappingAttributeBuilder inCreate(boolean inCreate)
    {
      this.inCreate = inCreate;
      return this;
    }
    
    public MappingAttributeBuilder inSearch(boolean inSearch)
    {
      this.inSearch = inSearch;
      return this;
    }

    public MappingAttributeBuilder binary(boolean binary)
    {
      this.binary = binary;
      return this;
    }

    public MappingAttributeBuilder multiValue(boolean multiValue)
    {
      this.multiValue = multiValue;
      return this;
    }

    public MappingAttributeBuilder rdn(boolean rdn)
    {
      this.rdn = rdn;
      return this;
    }

    public MappingAttributeBuilder encoder(Class<? extends MappingEncoder> encoder)
    {
      this.encoder = encoder;
      return this;
    }

    public MappingAttributeBuilder decoder(Class<? extends MappingDecoder> decoder)
    {
      this.decoder = decoder;
      return this;
    }

    public MappingAttribute build()
    {
      return new MappingAttribute(name, ldapName, siblings, inRead, inModify, inCreate, inSearch, binary, multiValue, rdn, encoder, decoder);
    }

  }

  public MappingAttribute()
  {
  }

  public MappingAttribute(String name, String ldapName, List<String> siblings, 
      boolean inRead, boolean inModify, boolean inCreate, boolean inSearch, 
      boolean binary, boolean multiValue, boolean rdn, 
      Class<? extends MappingEncoder> encoder, Class<? extends MappingDecoder> decoder)
  {
    this.name = name;
    this.ldapName = ldapName;
    this.siblings = siblings;
    this.inRead = inRead;
    this.inModify = inModify;
    this.inCreate = inCreate;
    this.inSearch = inSearch;
    this.binary = binary;
    this.multiValue = multiValue;
    this.rdn = rdn;
    this.encoder = encoder;
    this.decoder = decoder;
  }

  @XmlValue
  @XmlJavaTypeAdapter(TrimXmlAdapter.class)
  private String name;

  @XmlAttribute(name = "ldap-name")
  private String ldapName;
  
  @XmlAttribute(name = "sibling")
  private List<String> siblings;

  @XmlAttribute(name = "in-read")
  private boolean inRead = true;

  @XmlAttribute(name = "in-modify")
  private boolean inModify = true;

  @XmlAttribute(name = "in-create")
  private boolean inCreate = true;
  
  @XmlAttribute(name = "in-search")
  private boolean inSearch = true;

  @XmlAttribute(name = "is-binary")
  private boolean binary = false;

  @XmlAttribute(name = "is-multi-value")
  private boolean multiValue = false;

  @XmlAttribute(name = "is-rdn")
  private boolean rdn = false;

  @XmlAttribute
  private Class<? extends MappingEncoder> encoder = DefaultMappingEncoder.class;

  @XmlAttribute
  private Class<? extends MappingDecoder> decoder = DefaultMappingDecoder.class;

  public String getName()
  {
    return name;
  }

  public String getLdapName()
  {
    return MoreObjects.firstNonNull(ldapName, name);
  }

  public List<String> getSiblings()
  {
    if (siblings == null){
      siblings = ImmutableList.of();
    }
    return siblings;
  }

  public boolean isInRead()
  {
    return inRead;
  }

  public boolean isInModify()
  {
    return inModify;
  }

  public boolean isInCreate()
  {
    return inCreate;
  }

  public boolean isBinary()
  {
    return binary;
  }

  public boolean isMultiValue()
  {
    return multiValue;
  }

  public boolean isInSearch()
  {
    return inSearch;
  }

  public boolean isRdn()
  {
    return rdn;
  }

  public Class<? extends MappingEncoder> getEncoder()
  {
    return encoder;
  }

  public Class<? extends MappingDecoder> getDecoder()
  {
    return decoder;
  }
  
}
