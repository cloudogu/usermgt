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
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import de.triology.universeadm.*;
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
import de.triology.universeadm.mapping.IllegalQueryException;
import org.junit.rules.ExpectedException;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
        configuration = "classpath:de/triology/universeadm/shiro.001.ini",
        username = "trillian",
        password = "secret"
)
public class LDAPUserManagerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String BASEDN = "dc=hitchhiker,dc=com";
    private static final String LDIF_001 = "/de/triology/universeadm/user/test.001.ldif";
    private static final String LDIF_002 = "/de/triology/universeadm/user/test.002.ldif";
    private static final String LDIF_003 = "/de/triology/universeadm/user/test.003.ldif";
    private static final String LDIF_004 = "/de/triology/universeadm/user/test.004.ldif";
    private static final String LDIF_005 = "/de/triology/universeadm/user/test.005.ldif";
    private static final String MAPPING_001 = "de/triology/universeadm/user/mapping.001.xml";

    private EventBus eventBus;
    private Validator validator;
    private final LDAPHasher hasher = new PlainLDAPHasher();

    @Before
    public void before() {
        eventBus = mock(EventBus.class);
        validator = mock(Validator.class);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_001)
    public void testCreate() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User user = Users.createDent();
        User copy = Users.copy(user);
        manager.create(user);

        Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
        assertEntry(copy, entry);

        UserEvent event = new UserEvent(user, EventType.CREATE);
        verify(eventBus, times(1)).post(event);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_001)
    public void testCreateExternal() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User user = Users.createTrillexterno();
        User copy = Users.copy(user);
        manager.create(user);

        Entry entry = ldap.getConnection().getEntry("uid=trillexterno,ou=People,dc=hitchhiker,dc=com");
        assertEntry(copy, entry);

        UserEvent event = new UserEvent(user, EventType.CREATE);
        verify(eventBus, times(1)).post(event);
    }

    @Test()
    @LDAP(baseDN = BASEDN, ldif = LDIF_001)
    public void testCreateAlreadyExists() throws LDAPException {
        expectedException.expect(UniqueConstraintViolationException.class);
        expectedException.expectMessage("Constraints violated: ");
        expectedException.expectMessage("UNIQUE_EMAIL");
        expectedException.expectMessage("UNIQUE_USERNAME");

        LDAPUserManager manager = createUserManager();
        User user = Users.createDent();
        manager.create(user);
        manager.create(user);
    }

    @Test()
    @LDAP(baseDN = BASEDN, ldif = LDIF_001)
    public void testCreateEmailAlreadyExists() throws LDAPException {
        expectedException.expect(UniqueConstraintViolationException.class);
        expectedException.expectMessage("Constraints violated: ");
        expectedException.expectMessage("UNIQUE_EMAIL");

        LDAPUserManager manager = createUserManager();
        User user1 = Users.createDent();
        User user2 = Users.createDent2();
        manager.create(user1);
        manager.create(user2);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_002)
    public void testGet() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User expUser = Users.createDent();
        User user = manager.get(expUser.getUsername());
        assertUser(expUser, user);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_001)
    public void testNotFound() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User user = manager.get("dent");
        assertNull(user);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_002)
    public void testRemove() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User user = manager.get("dent");
        assertNotNull(user);
        manager.remove(user);
        Entry entry = ldap.getConnection().getEntry("uid=dent,ou=People,dc=hitchhiker,dc=com");
        assertNull(entry);
        UserEvent event = new UserEvent(user, EventType.REMOVE);
        verify(eventBus, times(1)).post(event);
    }

    @LDAP(baseDN = BASEDN, ldif = LDIF_004)
    @Test(expected = UserSelfRemoveException.class)
    public void testRemoveHimSelf() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User user = manager.get("trillian");
        assertNotNull(user);
        manager.remove(user);
    }

    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    @Test(expected = AuthorizationException.class)
    @SubjectAware(username = "dent", password = "secret")
    public void testSelfModify() throws LDAPException {
        User dent = Users.createDent();
        dent.setDisplayName("The dent");
        createUserManager().modify(dent);
    }

    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    @Test(expected = AuthorizationException.class)
    @SubjectAware(username = "dent", password = "secret")
    public void testSelfModifyMembership() throws LDAPException {
        User dent = Users.createDent();
        dent.setDisplayName("The dent");
        dent.getMemberOf().add("piloten");
        createUserManager().modify(dent);
    }

    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    @Test(expected = AuthorizationException.class)
    @SubjectAware(username = "dent", password = "secret")
    public void testModifyOtherUserUnprivileged() throws LDAPException {
        LDAPUserManager manager = createUserManager();
        User trillian = Users.createTrillian();
        trillian.setDisplayName("Tricia");
        trillian.setMail("tricia@hitchhiker.com");
        manager.modify(trillian);
    }

    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    @Test(expected = AuthorizationException.class)
    @SubjectAware(username = "dent", password = "secret")
    public void testGetOtherUserUnprivileged() throws LDAPException {
        createUserManager().get("tricia");
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_002)
    public void testModify() throws LDAPException {
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

    //FIXME tests

//    @Test
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    public void testGetAll() throws LDAPException {
//        LDAPUserManager manager = createUserManager();
//
//        List<User> users = manager.getAll();
//        assertNotNull(users);
//
//        List<User> expUsers = Lists.newArrayList(
//                Users.createDent(),
//                Users.createTrillian(),
//                Users.createTrillexterno()
//        );
//
//
//        assertEquals(expUsers.size(), users.size());
//
//        for (int i = 0; i < users.size(); i++) {
//            assertUser(expUsers.get(i), users.get(i));
//        }
//    }
//
//    @Test
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    public void testGetAllPaging() throws LDAPException {
//        LDAPUserManager manager = createUserManager();
//        PagedResultList<User> users = manager.getAll(0, 1);
//        assertNotNull(users);
//        assertEquals(0, users.getStart());
//        assertEquals(1, users.getLimit());
//        assertEquals(3, users.getTotalEntries());
//
//        List<User> entries = users.getEntries();
//        assertEquals(1, entries.size());
//
//        User expUserDent = Users.createDent();
//        assertUser(expUserDent, entries.get(0));
//
//        users = manager.getAll(1, 1);
//        assertEquals(1, users.getStart());
//        assertEquals(1, users.getLimit());
//        assertEquals(3, users.getTotalEntries());
//
//        entries = users.getEntries();
//        assertEquals(1, entries.size());
//
//        User expUserTricia = Users.createTrillian();
//        assertUser(expUserTricia, entries.get(0));
//    }
//
//    @Test
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    public void testSearch() throws LDAPException {
//        LDAPUserManager manager = createUserManager();
//        User expUser = Users.createTrillian();
//
//        List<User> users = manager.search(expUser.getUsername());
//        assertNotNull(users);
//        assertEquals(1, users.size());
//
//        assertUser(expUser, users.get(0));
//    }
//
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    @Test(expected = IllegalQueryException.class)
//    public void testSearchInvalidCharacters() throws LDAPException {
//        LDAPUserManager manager = createUserManager();
//        manager.search("tri(c)ia");
//    }
//
//    @Test
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    public void testModifyPassword() throws LDAPException {
//        LDAPUserManager manager = createUserManager();
//        User user = manager.get("tricia");
//        user.setPassword("secret");
//        manager.modify(user);
//        Entry entry = ldap.getConnection().getEntry("uid=tricia,ou=People,dc=hitchhiker,dc=com");
//        String pwd = entry.getAttributeValue("userPassword");
//        user = manager.get("tricia");
//        manager.modify(user);
//        entry = ldap.getConnection().getEntry("uid=tricia,ou=People,dc=hitchhiker,dc=com");
//        assertEquals(pwd, entry.getAttributeValue("userPassword"));
//    }
//
//    @Test
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    public void testFindExternal() throws LDAPException {
//        Entry entry = ldap.getConnection().getEntry("uid=trillexterno,ou=People,dc=hitchhiker,dc=com");
//        assertEquals("TRUE", entry.getAttributeValue("external"));
//    }
//
//    @Test
//    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
//    public void testSearchExternal() throws LDAPException {
//        LDAPUserManager manager = createUserManager();
//        User expUser = Users.createTrillexterno();
//
//        List<User> users = manager.search(expUser.getUsername());
//        assertNotNull(users);
//        assertEquals(1, users.size());
//        assertUser(expUser, users.get(0));
//    }

    private void assertUser(User expUser, User actUser) {
        assertNotNull(expUser);
        assertNotNull(actUser);
        assertEquals(expUser.getUsername(), actUser.getUsername());
        assertEquals(expUser.getDisplayName(), actUser.getDisplayName());
        assertEquals(expUser.getGivenname(), actUser.getGivenname());
        assertEquals(expUser.getSurname(), actUser.getSurname());
        assertEquals(expUser.getMail(), actUser.getMail());
        assertEquals(expUser.isPwdReset(), actUser.isPwdReset());
        assertEquals(expUser.isExternal(), actUser.isExternal());
    }

    private void assertEntry(User user, Entry entry) {
        assertNotNull(entry);
        assertEquals(user.getUsername(), entry.getAttributeValue("uid"));
        assertEquals(user.getDisplayName(), entry.getAttributeValue("cn"));
        assertEquals(user.getGivenname(), entry.getAttributeValue("givenName"));
        assertEquals(user.getSurname(), entry.getAttributeValue("sn"));
        assertEquals(user.getMail(), entry.getAttributeValue("mail"));
        assertEquals(user.getPassword(), entry.getAttributeValue("userPassword"));
        assertEquals(user.isExternal() ? "TRUE" : "FALSE", entry.getAttributeValue("external"));
    }

    private LDAPUserManager createUserManager() throws LDAPException {
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
