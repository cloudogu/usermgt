/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import com.github.legman.EventBus;
import de.triology.universeadm.BaseDirectory;
import java.io.File;
import java.io.IOException;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;

/**
 *
 * @author ssdorra
 */
@PrepareForTest(BaseDirectory.class)
@SubjectAware(configuration = "classpath:de/triology/universeadm/shiro.001.ini")
public class DefaultSettingsStoreTest
{
  
  private EventBus eventBus;
  private DefaultSettingsStore.SettingsStoreConfiguration configurarion;
  
  @Before
  public void prepare() throws IOException
  {
    eventBus = mock(EventBus.class);
    File directory = temp.newFolder();
    configurarion = new DefaultSettingsStore.SettingsStoreConfiguration(directory);
  }
  
  @Test(expected = UnauthorizedException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testGetNonAdministrator()
  {
    DefaultSettingsStore store = new DefaultSettingsStore(eventBus, configurarion);
    store.get();
  }
  
  @Test(expected = UnauthorizedException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testSetNonAdministrator()
  {
    DefaultSettingsStore store = new DefaultSettingsStore(eventBus, configurarion);
    store.set(new Settings(null, true, true, true));
  }
  
  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void testGet()
  {
    DefaultSettingsStore store = new DefaultSettingsStore(eventBus, configurarion);
    Settings settings = store.get();
    assertNotNull(settings);
  }
  
  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void testSet(){
    DefaultSettingsStore store = new DefaultSettingsStore(eventBus, configurarion);
    Settings oldSettings = store.get();
    Settings settings = new Settings(new Credentials("trillian", "secret"), true, true, true);
    store.set(settings);
    Settings other = store.get();
    assertEquals(settings, other);
    verify(eventBus, times(1)).post(new SettingsChangedEvent(settings, oldSettings));
  }
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
  
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  
}
