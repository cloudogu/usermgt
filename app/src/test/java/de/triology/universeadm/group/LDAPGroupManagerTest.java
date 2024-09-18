package de.triology.universeadm.group;

import com.github.legman.EventBus;
import com.github.sdorra.ldap.LDAP;
import de.triology.universeadm.*;
import org.junit.Test;
import org.junit.Rule;
import com.github.sdorra.ldap.LDAPRule;
import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;
import com.google.common.io.Resources;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import de.triology.universeadm.mapping.DefaultMapper;
import de.triology.universeadm.mapping.IllegalQueryException;
import de.triology.universeadm.mapping.Mapper;
import de.triology.universeadm.mapping.MapperFactory;
import de.triology.universeadm.mapping.Mapping;
import de.triology.universeadm.mapping.SimpleMappingConverterFactory;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import de.triology.universeadm.validation.Validator;
import java.util.List;
import javax.xml.bind.JAXB;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Before;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@SubjectAware(
  configuration = "classpath:de/triology/universeadm/shiro.001.ini",
  username = "trillian",
  password = "secret"
)
public class LDAPGroupManagerTest
{

  private static final String BASEDN = "dc=hitchhiker,dc=com";
  private static final String LDIF_001 = "/de/triology/universeadm/group/test.001.ldif";
  private static final String LDIF_002 = "/de/triology/universeadm/group/test.002.ldif";
  private static final String LDIF_003 = "/de/triology/universeadm/group/test.003.ldif";
  private static final String MAPPING_001 = "de/triology/universeadm/group/mapping.001.xml";

  private EventBus eventBus;
  private Validator validator;

  @Before
  public void before()
  {
    eventBus = mock(EventBus.class);
    validator = mock(Validator.class);
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void createTest() throws LDAPException {
    LDAPGroupManager groupManager = createGroupManager();
    Group heartOfGold = Groups.createHeartOfGold();
    groupManager.create(heartOfGold);
    Entry entry = ldap.getConnection().getEntry("cn=Heart Of Gold,ou=Groups,dc=hitchhiker,dc=com");
    assertEntry(heartOfGold, entry);
    GroupEvent event = new GroupEvent(heartOfGold, EventType.CREATE);
    verify(eventBus, times(1)).post(event);
  }

  @Test(expected = UnauthorizedException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  @SubjectAware(username = "dent", password = "secret")
  public void createTestWithoutAdminPrivileges() throws LDAPException
  {
    createGroupManager().create(Groups.createHeartOfGold());
  }

  @Test(expected = UniqueConstraintViolationException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_001)
  public void createTestWithConstraintViolation() throws LDAPException
  {
    LDAPGroupManager groupManager = createGroupManager();
    groupManager.create(Groups.createHeartOfGold());
    groupManager.create(Groups.createHeartOfGold());
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void getTest() throws LDAPException
  {
    Group group = createGroupManager().get("Heart Of Gold");
    assertEquals(Groups.createHeartOfGold(), group);
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  public void getTestNotFound() throws LDAPException
  {
    assertNull(createGroupManager().get("Brockian Ultra-Cricket"));
  }

  @Test(expected = UnauthorizedException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_002)
  @SubjectAware(username = "dent", password = "secret")
  public void getTestWithoutAdminPrivileges() throws LDAPException
  {
    createGroupManager().get("Heart Of Gold");
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void queryTest() throws LDAPException
  {
    PaginationResult<Group> result = createGroupManager().query(new PaginationQuery(1, 10));
    assertNotNull(result);
    assertEquals(2, result.getEntries().size());
    assertEquals(2, result.getTotalEntries());
    assertThat(result.getEntries(), contains(Groups.createBrockianUltraCricket(), Groups.createHeartOfGold()));
  }

  @Test(expected = UnauthorizedException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @SubjectAware(username = "dent", password = "secret")
  public void queryTestWithoutAdminPrivileges() throws LDAPException
  {
    createGroupManager().query(new PaginationQuery(1, 10));
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void queryTestWithQuery() throws LDAPException{
    PaginationQuery query = new PaginationQuery(1, 10, "Heart", null, null, null, false);
    PaginationResult<Group> result  = createGroupManager().query(query);
    assertEquals(1, result.getEntries().size());
    assertEquals(1, result.getTotalEntries());
    assertThat(result.getEntries(), contains(Groups.createHeartOfGold()));
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void queryTestNotFound() throws LDAPException{
    PaginationQuery query = new PaginationQuery(1, 10, "Marvin", null, null, null, false);
    PaginationResult<Group> result  = createGroupManager().query(query);
    assertEquals(0, result.getEntries().size());
    assertEquals(0, result.getTotalEntries());
    assertThat(result.getEntries(), empty());
  }

  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @Test(expected = IllegalQueryException.class)
  public void queryTestIllegalCharacters() throws LDAPException{
    PaginationQuery query = new PaginationQuery(1, 10, "Mar(v)in", null, null, null, false);
    createGroupManager().query(query);
  }

  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void removeTest() throws LDAPException
  {
    final Group group = Groups.createHeartOfGold();
    createGroupManager().remove(group);
    assertNull(ldap.getConnection().getEntry("cn=Heart Of Gold,ou=Groups,dc=hitchhiker,dc=com"));
    verify(eventBus).post(new GroupEvent(group, EventType.REMOVE));
  }

  @Test(expected = UnauthorizedException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @SubjectAware(username = "dent", password = "secret")
  public void removeTestWithoutAdminPrivileges() throws LDAPException
  {
    createGroupManager().remove(Groups.createHeartOfGold());
  }

  @Test(expected = CannotRemoveException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void removeTestUndeletableGroup() throws LDAPException
  {
    LDAPGroupManager groupManager = createGroupManager();

    final Group group = Groups.createBrockianUltraCricket();
    groupManager.remove(group);

    assertNotNull(ldap.getConnection().getEntry("cn=Heart Of Gold,ou=Groups,dc=hitchhiker,dc=com"));
  }


  @Test
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  public void modifyTest() throws LDAPException
  {
    Group group = Groups.createHeartOfGold();
    Group modified = Groups.createHeartOfGold();
    modified.setDescription("The Heart of Gold is 150 metres long. It is shaped like a running shoe, and it is generally rather white.");
    createGroupManager().modify(modified);
    Entry entry = ldap.getConnection().getEntry("cn=Heart Of Gold,ou=Groups,dc=hitchhiker,dc=com");
    assertEntry(modified, entry);
    verify(eventBus).post(new GroupEvent(modified, group));
  }

  @Test(expected = UnauthorizedException.class)
  @LDAP(baseDN = BASEDN, ldif = LDIF_003)
  @SubjectAware(username = "dent", password = "secret")
  public void modifyTestWithoutAdminPrivileges() throws LDAPException
  {
    createGroupManager().modify(Groups.createHeartOfGold());
  }

  private void assertEntry(Group group, Entry entry){
    assertEquals(group.getName(), entry.getAttributeValue("cn"));
    assertEquals(group.getDescription(), entry.getAttributeValue("description"));
    assertThat(entry.getAttributeValues("member"), arrayContaining("dent", "trillian"));
  }

  private LDAPGroupManager createGroupManager() throws LDAPException
  {
    String groupsdn = "ou=Groups,".concat(BASEDN);
    LDAPConnectionStrategy strategy = mock(LDAPConnectionStrategy.class);
    when(strategy.get()).thenReturn(ldap.getConnection());
    LDAPConfiguration config = new LDAPConfiguration(
      "localhost", 10389, "cn=Directory Manager",
      "manager123", null, groupsdn
    );
    Mapping mapping = JAXB.unmarshal(Resources.getResource(MAPPING_001), Mapping.class);
    Mapper<Group> mapper = new DefaultMapper<>(new SimpleMappingConverterFactory(), mapping, Group.class, groupsdn);
    MapperFactory mapperFactory = mock(MapperFactory.class);
    when(mapperFactory.createMapper(Group.class, groupsdn)).thenReturn(mapper);
    UndeletableGroupManager undeletableGroupManager = mock(UndeletableGroupManager.class);
    when(undeletableGroupManager.isGroupUndeletable("Brockian Ultra-Cricket")).thenReturn(true);
    return new LDAPGroupManager(strategy, config, undeletableGroupManager, mapperFactory, validator, eventBus);
  }


  @Rule
  public LDAPRule ldap = new LDAPRule();

  @Rule
  public ShiroRule shiro = new ShiroRule();

}
