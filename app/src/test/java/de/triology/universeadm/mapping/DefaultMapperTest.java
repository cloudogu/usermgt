/* 
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * http://www.scm-manager.com
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
import de.triology.universeadm.user.Users;
import java.util.List;
import java.util.NoSuchElementException;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
      attr("surname", "sn"),
      attrb("displayName").ldapName("cn").sibling("displayName").build(),
      attrb("pwdReset").decoder(LDAPBooleanConverter.class).encoder(LDAPBooleanConverter.class).build()
    );
    return new DefaultMapper<>(new SimpleMappingConverterFactory(), mapping, User.class, "dc=hitchhiker,dc=com");
  }

  private User createUser()
  {
    return Users.createDent();
  }

  private Entry createEntry()
  {
    return new Entry(
            "uid=dent,dc=hitchhiker,dc=com",
            new Attribute("uid", "dent"),
            new Attribute("givenname", "Arthur"),
            new Attribute("sn", "Dent"),
            new Attribute("pwdReset", "TRUE")
    );
  }

  @Test
  public void testGetReturningAttributes()
  {
    DefaultMapper<User> mapper = createDefaultMapper();
    List<String> attributes = mapper.getReturningAttributes();
    assertThat(attributes, containsInAnyOrder("uid", "givenname", "sn", "cn", "pwdReset"));
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
    assertEquals(user.getDisplayName(), entry.getAttributeValue("cn"));
    assertEquals("TRUE", entry.getAttributeValue("pwdReset"));
    // test sibling
    assertEquals(user.getDisplayName(), entry.getAttributeValue("displayName"));
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
    assertTrue(user.isPwdReset());
  }
  
  @Test(expected = MappingException.class)
  public void testWithoutRdn(){
    Mapping mapping = createMapping(
            attr("username"),
            attr("givenname"),
            attr("surname", "sn")
    );
    new DefaultMapper<>(new SimpleMappingConverterFactory(), mapping, User.class, "dc=hitchhiker,dc=com");
  }
  
  @Test(expected = MappingException.class)
  public void testMultipleRdn(){
    Mapping mapping = createMapping(
            attrb("username").rdn(true).build(),
            attr("givenname"),
            attrb("surname").rdn(true).build()
    );
    new DefaultMapper<>(new SimpleMappingConverterFactory(), mapping, User.class, "dc=hitchhiker,dc=com");
  }

  @Test
  public void testGetModifications()
  {
    Mapper<User> mapper = createDefaultMapper();
    User user = createUser();
    List<Modification> modifications = mapper.getModifications(user);
    assertNotNull(modifications);
    assertEquals(5, modifications.size());
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
    Modification cn = find(modifications, "cn");
    assertNotNull(cn);
    assertEquals(user.getDisplayName(), cn.getAttribute().getValue());
    // sibling
    Modification displayName = find(modifications, "displayName");
    assertNotNull(displayName);
    assertEquals(user.getDisplayName(), displayName.getAttribute().getValue());
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
