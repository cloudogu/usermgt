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

package de.triology.universeadm.user;

import com.github.legman.EventBus;
import com.google.common.io.Resources;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import de.triology.universeadm.EntityAlreadyExistsException;
import de.triology.universeadm.EventType;
import de.triology.universeadm.LDAPConfiguration;
import de.triology.universeadm.LDAPConnectionStrategy;
import de.triology.universeadm.LDAPHasher;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.mapping.DefaultMapper;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.Mapping;
import de.triology.universeadm.mapping.SimpleMappingConverterFactory;
import de.triology.universeadm.validation.Validator;
import java.util.List;
import javax.xml.bind.JAXB;
import org.apache.shiro.authz.AuthorizationException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;
import com.github.sdorra.ldap.LDAP;
import com.github.sdorra.ldap.LDAPRule;
import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import de.triology.universeadm.PlainLDAPHasher;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
  configuration = "classpath:de/triology/universeadm/shiro.001.ini", 
  username = "trillian", 
  password = "secret"
)
public class LDAPUserManagerTest
{

  private static final String BASEDN = "dc=hitchhiker,dc=com";
  private static final String LDIF_001 = "/de/triology/universeadm/user/test.001.ldif";
  private static final String LDIF_002 = "/de/triology/universeadm/user/test.002.ldif";
  private static final String LDIF_003 = "/de/triology/universeadm/user/test.003.ldif";
  private static final String MAPPING_001 = "de/triology/universeadm/user/mapping.001.xml";

  private EventBus eventBus;
  private Validator validator;
  private final LDAPHasher hasher = new PlainLDAPHasher();

  @Before
  public void before()
  {
    eventBus = mock(EventBus.class);
    validator = mock(Validator.class);
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testCreate() throws LDAPException
  {
    LDAPUserManager manager = createUserManager();
    User user = Users.createDent();
    manager.create(user);

    Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
    assertEntry(entry);
    UserEvent event = new UserEvent(user, EventType.CREATE);
    verify(eventBus, times(1)).post(event);
  }
  
  @Test(expected = EntityAlreadyExistsException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testCreateAlreadyExists() throws LDAPException
  {
    LDAPUserManager manager = createUserManager();
    User user = Users.createDent();
    manager.create(user);
    manager.create(user);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void testGet() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertUser(user);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void testNotFound() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertNull(user);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void testRemove() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    assertNotNull(user);
    manager.remove(user);
    Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
    assertNull(entry);
    UserEvent event = new UserEvent(user, EventType.REMOVE);
    verify(eventBus, times(1)).post(event);
  }

  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @SubjectAware(username = "dent", password = "secret")
  public void testSelfModify() throws LDAPException
  {
    User dent = Users.createDent();
    dent.setDisplayName("The dent");
    createUserManager().modify(dent);
  }
  
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @Test(expected = AuthorizationException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testSelfModifyMembership() throws LDAPException
  {
    User dent = Users.createDent();
    dent.setDisplayName("The dent");
    dent.getMemberOf().add("piloten");
    createUserManager().modify(dent);
  }
  
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @Test(expected = AuthorizationException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testModifyOtherUserUnprivileged() throws LDAPException
  {
    User tricia = Users.createTrillian();
    tricia.setDisplayName("Tricia");
    createUserManager().modify(tricia);
  }
  
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @Test(expected = AuthorizationException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testGetOtherUserUnprivileged() throws LDAPException
  {
    createUserManager().get("tricia");
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void testModify() throws LDAPException{
    LDAPUserManager manager = createUserManager();
    User user = manager.get("dent");
    User old = manager.get("dent");
    assertNotNull(user);
    user.setDisplayName("Dent, Arthur");
    manager.modify(user);
    Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
    assertNotNull(entry);
    assertEquals("Dent, Arthur", entry.getAttributeValue("cn"));
    UserEvent event = new UserEvent(user, old);
    verify(eventBus, times(1)).post(event);
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testGetAll() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    List<User> users = manager.getAll();
    assertNotNull(users);
    assertEquals(2, users.size());
    assertUser(users.get(0));
    assertEquals("tricia", users.get(1).getUsername());
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testGetAllPaging() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    PagedResultList<User> users = manager.getAll(0, 1);
    assertNotNull(users);
    assertEquals(0, users.getStart());
    assertEquals(1, users.getLimit());
    assertEquals(2, users.getTotalEntries());
    List<User> entries = users.getEntries();
    assertEquals(1, entries.size());
    assertUser(entries.get(0));
    
    users = manager.getAll(1, 1);
    assertEquals(1, users.getStart());
    assertEquals(1, users.getLimit());
    assertEquals(2, users.getTotalEntries());
    entries = users.getEntries();
    assertEquals(1, entries.size());
    assertEquals("tricia", entries.get(0).getUsername());
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testSearch() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    List<User> users = manager.search("tricia");
    assertNotNull(users);
    assertEquals(1, users.size());
    assertEquals("tricia", users.get(0).getUsername());
  }
  
  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void testModifyPassword() throws LDAPException {
    LDAPUserManager manager = createUserManager();
    User user = manager.get("tricia");
    user.setPassword("secret");
    manager.modify(user);
    Entry entry = ldap.getConnection().getEntry("uid=tricia,ou=People,dc=hitchhiker,dc=com");
    String pwd = entry.getAttributeValue("userPassword");
    user = manager.get("tricia");
    manager.modify(user);
    entry = ldap.getConnection().getEntry("uid=tricia,ou=People,dc=hitchhiker,dc=com");
    assertEquals(pwd, entry.getAttributeValue("userPassword"));
  }

  private void assertUser(User user){
    assertNotNull(user);
    assertEquals("dent", user.getUsername());
    assertEquals("Arthur Dent", user.getDisplayName());
    assertEquals("Arthur", user.getGivenname());
    assertEquals("Dent", user.getSurname());
    assertEquals("arthur.dent@hitchhiker.com", user.getMail());
  }
  
  private void assertEntry(Entry entry){
    assertNotNull(entry);
    assertEquals("dent", entry.getAttributeValue("uid"));
    assertEquals("Arthur Dent", entry.getAttributeValue("cn"));
    assertEquals("Arthur", entry.getAttributeValue("givenName"));
    assertEquals("Dent", entry.getAttributeValue("sn"));
    assertEquals("arthur.dent@hitchhiker.com", entry.getAttributeValue("mail"));
    assertEquals("hitchhiker123", entry.getAttributeValue("userPassword"));
  }
  
  private LDAPUserManager createUserManager() throws LDAPException
  {
    String peopledn = "ou=People,".concat(BASEDN);
    LDAPConnectionStrategy strategy = mock(LDAPConnectionStrategy.class);
    when(strategy.get()).thenReturn(ldap.getConnection());
    LDAPConfiguration config = new LDAPConfiguration(
      "localhost", 10389, "cn=Directory Manager", 
      "manager123", peopledn, null
    );
    Mapping mapping = JAXB.unmarshal(Resources.getResource(MAPPING_001), Mapping.class);
    Mapper<User> mapper = new DefaultMapper<>(new SimpleMappingConverterFactory(), mapping, User.class, peopledn);
    MapperFactory mapperFactory = mock(MapperFactory.class);
    when(mapperFactory.createMapper(User.class, peopledn)).thenReturn(mapper);
    return new LDAPUserManager(strategy, config, hasher, mapperFactory, validator, eventBus);
  }

  @Rule
  public LDAPRule ldap = new LDAPRule();
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
}
