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

import com.google.common.collect.Lists;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.github.sdorra.ldap.LDAP;
import com.github.sdorra.ldap.LDAPRule;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FieldDescriptor.class)
public class MemberMappingConverterTest
{
  
  private static final String BASEDN = "dc=hitchhiker,dc=com";
  
  private static final String LDIF_001 = "/de/triology/universeadm/mapping/test.001.ldif";
  
  @Test
  public void testDecodeFromString()
  {
    MemberMappingConverter converter = createConverterForDecode();
    assertEquals("tricia", converter.decodeFromString(null, "uid=tricia,ou=People,dc=hitchhiker,dc=com"));
    assertEquals("dent", converter.decodeFromString(null, "uid=dent,ou=People,dc=hitchhiker,dc=com"));
  }
  
  @Test(expected = MappingException.class)
  public void testDecodeFromInvalidString()
  {
    MemberMappingConverter converter = createConverterForDecode();
    converter.decodeFromString(null, "uidtricia,ouPeople,dchitchhiker,dccom");
  }
  
  @Test(expected = MappingException.class)
  public void testDecodeFromInvalidString2()
  {
    MemberMappingConverter converter = createConverterForDecode();
    converter.decodeFromString(null, "uid=dentou=Peopledc=hitchhikerdc=com");
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testDecodeFromMultiString(){
    MemberMappingConverter converter = createConverterForDecode();
    FieldDescriptor desc = mock(FieldDescriptor.class);
    when(desc.isSubClassOf(Collection.class)).thenReturn(Boolean.TRUE);
    when(desc.isSubClassOf(ArrayList.class)).thenReturn(Boolean.TRUE);
    Object result = converter.decodeFromMultiString(desc, new String[]{
      "uid=tricia,ou=People,dc=hitchhiker,dc=com",
      "uid=dent,ou=People,dc=hitchhiker,dc=com"
    });
    assertNotNull(result);
    assertTrue(result instanceof ArrayList);
    List<String> members = (List<String>) result;
    assertThat(members, contains("tricia", "dent"));
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testEncodeAsString() throws LDAPException {
    MemberMappingConverter converter = createConverterForEncode();
    assertEquals("uid=tricia,ou=People,dc=hitchhiker,dc=com", converter.encodeAsString("tricia"));
    assertEquals("uid=dent,ou=People,dc=hitchhiker,dc=com", converter.encodeAsString("dent"));
    assertNull(converter.encodeAsString("hansolo"));
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testEncodeAsMultiString() throws LDAPException {
    MemberMappingConverter converter = createConverterForEncode();
    List<String> list = Lists.newArrayList("tricia", "dent", "hansolo");
    String[] array = converter.encodeAsMultiString(list);
    assertThat(
      array, 
      allOf(
        hasItemInArray("uid=tricia,ou=People,dc=hitchhiker,dc=com"),
        hasItemInArray("uid=dent,ou=People,dc=hitchhiker,dc=com")
      )
    );
  }
  
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  @Test(expected = MappingException.class)
  public void testEncodeInvalidAsMultiString() throws LDAPException {
    MemberMappingConverter converter = createConverterForEncode();
    converter.encodeAsMultiString("test");
  }
  
  private MemberMappingConverter createConverterForDecode(){
    MapperFactory factory = mock(MapperFactory.class);
    LDAPConfiguration config = mock(LDAPConfiguration.class);
    when(config.getUserBaseDN()).thenReturn("ou=People,dc=hitchhiker,dc=com");
    return new MemberMappingConverter(null, config, factory);    
  }
  
  @SuppressWarnings("unchecked")
  private MemberMappingConverter createConverterForEncode() throws LDAPException{
    Mapper<User> mapper = mock(Mapper.class);
    when(mapper.getRDNName()).thenReturn("uid");
    when(mapper.getParentDN()).thenReturn("ou=People,dc=hitchhiker,dc=com");
    when(mapper.getBaseFilter()).thenReturn(Filter.create("(objectClass=organizationalPerson)"));
    MapperFactory factory = mock(MapperFactory.class);
    when(factory.createMapper(User.class, "ou=People,dc=hitchhiker,dc=com")).thenReturn(mapper);
    LDAPConfiguration config = mock(LDAPConfiguration.class);
    when(config.getUserBaseDN()).thenReturn("ou=People,dc=hitchhiker,dc=com");
    LDAPConnectionStrategy strategy = mock(LDAPConnectionStrategy.class);
    when(strategy.get()).thenReturn(rule.getConnection());
    return new MemberMappingConverter(strategy, config, factory);
  }
  
  @Rule
  public LDAPRule rule = new LDAPRule();
  
}
