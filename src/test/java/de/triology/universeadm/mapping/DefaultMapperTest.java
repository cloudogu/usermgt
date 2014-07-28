/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.mapping;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import de.triology.universeadm.user.User;
import java.util.List;
import java.util.NoSuchElementException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author ssdorra
 */
public class DefaultMapperTest
{

  private static final List<String> OBJECTCLASSES
          = ImmutableList.of("top", "person", "inetOrgPerson", "organizationalPerson");

  private Mapping createMapping(MappingAttribute attribute, MappingAttribute... attributes)
  {
    return new Mapping(OBJECTCLASSES, Lists.asList(attribute, attributes));
  }

  private MappingAttribute attr(String name, String ldapName)
  {
    return new MappingAttribute.MappingAttributeBuilder(name).ldapName(ldapName).build();
  }

  private MappingAttribute attr(String name)
  {
    return new MappingAttribute.MappingAttributeBuilder(name).build();
  }

  private MappingAttribute.MappingAttributeBuilder attrb(String name)
  {
    return new MappingAttribute.MappingAttributeBuilder(name);
  }

  private DefaultMapper<User> createDefaultMapper()
  {
    Mapping mapping = createMapping(
            attrb("username").ldapName("uid").inModify(false).rdn(true).build(),
            attr("givenname"),
            attr("surname", "sn")
    );
    return new DefaultMapper<>(mapping, User.class, "dc=hitchhiker,dc=com");
  }

  private User createUser()
  {
    return new User(
            "dent", "Arthur Dent", "Arthur", "Dent",
            "arthur.dent@hitchhiker.com", "hitchhiker123"
    );
  }

  private Entry createEntry()
  {
    return new Entry(
            "uid=dent,dc=hitchhiker,dc=com",
            new Attribute("uid", "dent"),
            new Attribute("givenname", "Arthur"),
            new Attribute("sn", "Dent")
    );
  }

  @Test
  public void testGetReturningAttributes()
  {
    DefaultMapper<User> mapper = createDefaultMapper();
    List<String> attributes = mapper.getReturningAttributes();
    assertThat(attributes, containsInAnyOrder("uid", "givenname", "sn"));
  }

  @Test
  public void testConvertToEntry()
  {
    DefaultMapper<User> mapper = createDefaultMapper();
    User user = createUser();
    Entry entry = mapper.convert(user);
    assertEquals("uid=dent,dc=hitchhiker,dc=com", entry.getDN());
    assertEquals(user.getUsername(), entry.getAttributeValue("uid"));
    assertEquals(user.getGivenname(), entry.getAttributeValue("givenname"));
    assertEquals(user.getSurname(), entry.getAttributeValue("sn"));
  }
  
  @Test(expected = MappingException.class)
  public void testConvertToEntryWithoutRdn()
  {
    DefaultMapper<User> mapper = createDefaultMapper();
    User user = createUser();
    user.setUsername(null);
    mapper.convert(user);
  }

  @Test
  public void testConvertToObject()
  {
    DefaultMapper<User> mapper = createDefaultMapper();
    Entry entry = createEntry();
    User user = mapper.convert(entry);
    assertEquals("dent", user.getUsername());
    assertEquals("Arthur", user.getGivenname());
    assertEquals("Dent", user.getSurname());
  }
  
  @Test(expected = MappingException.class)
  public void testWithoutRdn(){
    Mapping mapping = createMapping(
            attr("username"),
            attr("givenname"),
            attr("surname", "sn")
    );
    new DefaultMapper<>(mapping, User.class, "dc=hitchhiker,dc=com");
  }
  
  @Test(expected = MappingException.class)
  public void testMultipleRdn(){
    Mapping mapping = createMapping(
            attrb("username").rdn(true).build(),
            attr("givenname"),
            attrb("surname").rdn(true).build()
    );
    new DefaultMapper<>(mapping, User.class, "dc=hitchhiker,dc=com");
  }

  @Test
  public void testGetModifications()
  {
    Mapper<User> mapper = createDefaultMapper();
    User user = createUser();
    List<Modification> modifications = mapper.getModifications(user);
    assertNotNull(modifications);
    assertEquals(2, modifications.size());
    Modification username = find(modifications, "uid");
    assertNull(username);
    Modification givenname = find(modifications, "givenname");
    assertNotNull(givenname);
    assertEquals(user.getGivenname(), givenname.getAttribute().getValue());
    assertEquals(ModificationType.REPLACE, givenname.getModificationType());
    Modification sn = find(modifications, "sn");
    assertNotNull(sn);
    assertEquals(user.getSurname(), sn.getAttribute().getValue());
    assertEquals(ModificationType.REPLACE, sn.getModificationType());
  }

  private Modification find(List<Modification> modifications, final String name)
  {
    Modification m = null;
    try
    {
      m = Iterables.find(modifications, new Predicate<Modification>()
      {

        @Override
        public boolean apply(Modification input)
        {
          return input.getAttributeName().equalsIgnoreCase(name);
        }
      });
    }
    catch (NoSuchElementException ex)
    {

    }
    return m;
  }

}
