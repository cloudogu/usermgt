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

import com.google.common.io.Resources;
import com.unboundid.ldap.sdk.Filter;
import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.Stage;
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
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
    DefaultMapperFactory dcf = new DefaultMapperFactory(new SimpleMappingConverterFactory(), Stage.DEVELOPMENT);
    Mapper<User> m1 = dcf.createMapper(User.class, "ou=People");
    Mapper<User> m2 = dcf.createMapper(User.class, "ou=People");
    assertNotSame(m1, m2);
  }
  
}
