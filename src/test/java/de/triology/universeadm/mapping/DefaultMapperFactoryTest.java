/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.mapping;

import com.google.common.io.Resources;
import com.unboundid.ldap.sdk.Filter;
import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.user.User;
import javax.xml.bind.JAXB;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author ssdorra
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BaseDirectory.class)
public class DefaultMapperFactoryTest
{

  private static final String MAPPING_001 = "de/triology/universeadm/mapping/mapping.001.xml";
  
  private final DefaultMapperFactory factory = new DefaultMapperFactory(new SimpleMappingConverterFactory());
  
  @Before
  public void prepareBaseDirectory(){
    PowerMockito.mockStatic(BaseDirectory.class);
    Mapping mapping = JAXB.unmarshal(Resources.getResource(MAPPING_001), Mapping.class);
    when(BaseDirectory.getConfiguration("mapping/user.xml", Mapping.class)).thenReturn(mapping);
  }
  
  @Test
  public void testCreate()
  {
    Mapper<User> mapper = factory.createMapper(User.class, "ou=People");
    assertEquals("ou=People", mapper.getParentDN());
    assertEquals(Filter.createPresenceFilter("username"), mapper.getBaseFilter());
  }
  
  @Test
  public void testCacheEnabled(){
    Mapper<User> m1 = factory.createMapper(User.class, "ou=People");
    Mapper<User> m2 = factory.createMapper(User.class, "ou=People");
    assertSame(m1, m2);
  }
  
  @Test
  public void testCacheDisabled(){
    // how ?
  }
  
}
