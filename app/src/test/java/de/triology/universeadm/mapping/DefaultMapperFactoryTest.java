package de.triology.universeadm.mapping;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.io.Resources;

import com.unboundid.ldap.sdk.Filter;

import de.triology.universeadm.Stage;
import de.triology.universeadm.mapping.DefaultMapperFactory.MappingProvider;
import de.triology.universeadm.user.User;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

//~--- JDK imports ------------------------------------------------------------

import javax.xml.bind.JAXB;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultMapperFactoryTest
{

  /** Field description */
  private static final String MAPPING_001 =
    "de/triology/universeadm/mapping/mapping.001.xml";

  /** Field description */
  private static Mapping mapping;

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   */
  @BeforeClass
  public static void prpare()
  {
    mapping = JAXB.unmarshal(Resources.getResource(MAPPING_001), Mapping.class);
  }

  /**
   * Method description
   *
   */
  @Test
  public void testCacheDisabled()
  {
    DefaultMapperFactory dcf = create(Stage.DEVELOPMENT);
    Mapper<User> m1 = dcf.createMapper(User.class, "ou=People");
    Mapper<User> m2 = dcf.createMapper(User.class, "ou=People");

    assertNotSame(m1, m2);
  }

  /**
   * Method description
   *
   */
  @Test
  public void testCacheEnabled()
  {
    DefaultMapperFactory factory = create();
    Mapper<User> m1 = factory.createMapper(User.class, "ou=People");
    Mapper<User> m2 = factory.createMapper(User.class, "ou=People");

    assertSame(m1, m2);
  }

  /**
   * Method description
   *
   */
  @Test
  public void testCreate()
  {
    Mapper<User> mapper = create().createMapper(User.class, "ou=People");

    assertEquals("ou=People", mapper.getParentDN());
    assertEquals(Filter.createPresenceFilter("username"),
      mapper.getBaseFilter());
  }

  /**
   * Method description
   *
   *
   * @param stage
   *
   * @return
   */
  private DefaultMapperFactory create(Stage stage)
  {
    MappingProvider mp = mock(MappingProvider.class);

    when(mp.getMapping(User.class)).thenReturn(mapping);

    return new DefaultMapperFactory(new SimpleMappingConverterFactory(), mp,
      stage);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  private DefaultMapperFactory create()
  {
    return create(Stage.PRODUCTION);
  }
}
