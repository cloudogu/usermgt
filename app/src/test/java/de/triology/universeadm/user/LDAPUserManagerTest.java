package de.triology.universeadm.user;

import com.github.legman.EventBus;
import com.google.common.io.Resources;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import de.triology.universeadm.*;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.mapping.DefaultMapper;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.Mapping;
import de.triology.universeadm.mapping.SimpleMappingConverterFactory;
import de.triology.universeadm.user.imports.FieldConstraintViolationException;
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
    private GroupManager groupManager;
    private Validator validator;
    private final LDAPHasher hasher = new PlainLDAPHasher();

    @Before
    public void before() {
        eventBus = mock(EventBus.class);
        validator = mock(Validator.class);
        groupManager = mock(GroupManager.class);
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
        expectedException.expect(FieldConstraintViolationException.class);
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
        expectedException.expect(FieldConstraintViolationException.class);
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

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    public void testQueryWithPaging() throws LDAPException {
        LDAPUserManager manager = createUserManager();

        PaginationResult<User> result = manager.query(new PaginationQuery(1, 1));
        assertNotNull(result);
        assertEquals(3, result.getTotalEntries());

        List<User> entries = result.getEntries();
        assertEquals(1, entries.size());

        User expUserDent = Users.createDent();
        assertUser(expUserDent, entries.get(0));

        result = manager.query(new PaginationQuery(2, 1));
        assertEquals(3, result.getTotalEntries());

        entries = result.getEntries();
        assertEquals(1, entries.size());

        User expUserTricia = Users.createTrillian();
        assertUser(expUserTricia, entries.get(0));
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

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    public void testFindExternal() throws LDAPException {
        Entry entry = ldap.getConnection().getEntry("uid=trillexterno,ou=People,dc=hitchhiker,dc=com");
        assertEquals("TRUE", entry.getAttributeValue("external"));
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    public void checkConstraintsShouldReturnNoExceptionForCreateAndNoConstraintsAreViolated() throws LDAPException {
        LDAPUserManager sut = createUserManager();
        User user = new User("denvercoder9");
        user.setMail("denvercoder9@hitchhiker.com");
        user.setPassword("alligator3");
        user.setGivenname("Denver");
        user.setSurname("Coder");

        sut.checkConstraints(user, Constraint.Category.CREATE);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    public void checkConstraintsShouldReturnNoExceptionForModifyAndNoConstraintsAreViolated() throws LDAPException {
        LDAPUserManager sut = createUserManager();
        User user = new User("tricia");
        user.setMail("tricia@hitchhiker.com");
        user.setPassword("alligator4"); // look, a new password!
        user.setGivenname("Tricia");
        user.setSurname("McMillan");

        sut.checkConstraints(user, Constraint.Category.MODIFY);
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    public void checkConstraintsShouldReturnExceptionForCreateForInvalidMail() throws LDAPException {
        LDAPUserManager sut = createUserManager();
        User user = new User("denvercoder9");
        user.setMail("WwW.iKnoWtHeInTeRNeT.cOm");
        user.setPassword("alligator3");
        user.setGivenname("Denver");
        user.setSurname("Coder");

        try {
            sut.checkConstraints(user, Constraint.Category.CREATE);
            fail("expected FieldConstraintViolationException to be fired");
        } catch (FieldConstraintViolationException e) {
            assertEquals(1, e.violated.length);
            assertEquals(Constraint.ID.VALID_EMAIL, e.violated[0]);
        }
    }

    @Test
    @LDAP(baseDN = BASEDN, ldif = LDIF_003)
    public void checkConstraintsShouldReturnExceptionForModifyForInvalidMail() throws LDAPException {
        LDAPUserManager sut = createUserManager();
        User user = new User("tricia");
        user.setMail("triciaInvalid");
        user.setPassword("alligator4");
        user.setGivenname("Tricia");
        user.setSurname("McMillan");

        try {
            sut.checkConstraints(user, Constraint.Category.MODIFY);
            fail("expected FieldConstraintViolationException to be fired");
        } catch (FieldConstraintViolationException e) {
            assertEquals(1, e.violated.length);
            assertEquals(Constraint.ID.VALID_EMAIL, e.violated[0]);
        }
    }

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
        return new LDAPUserManager(strategy, config, hasher, mapperFactory, validator, eventBus, groupManager);
    }

    @Rule
    public LDAPRule ldap = new LDAPRule();

    @Rule
    public ShiroRule shiro = new ShiroRule();
}
