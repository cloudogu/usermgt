/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import com.github.legman.EventBus;
import java.io.File;
import java.io.IOException;
import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.github.sdorra.shiro.ShiroRule;
import com.github.sdorra.shiro.SubjectAware;

/**
 *
 * @author ssdorra
 */
@SubjectAware(configuration = "classpath:de/triology/universeadm/shiro.001.ini")
public class DefaultSettingsStoreTest
{
  
  private EventBus eventBus;
  private CredentialsChecker checker;
  private DefaultSettingsStore.SettingsStoreConfiguration configurarion;
  
  @Before
  public void prepare() throws IOException
  {
    eventBus = mock(EventBus.class);
    checker = mock(CredentialsChecker.class);
    File directory = temp.newFolder();
    configurarion = new DefaultSettingsStore.SettingsStoreConfiguration(directory);
  }
  
  @Test(expected = UnauthorizedException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testGetNonAdministrator()
  {
    DefaultSettingsStore store = createSettingsStore();
    store.get();
  }
  
  @Test(expected = UnauthorizedException.class)
  @SubjectAware(username = "dent", password = "secret")
  public void testSetNonAdministrator()
  {
    DefaultSettingsStore store = createSettingsStore();
    store.set(new Settings(null, true, true, true));
  }
  
  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void testGet()
  {
    DefaultSettingsStore store = createSettingsStore();
    Settings settings = store.get();
    assertNotNull(settings);
  }
  
  @Test
  @SubjectAware(username = "trillian", password = "secret")
  public void testSet() throws IOException{
    DefaultSettingsStore store = createSettingsStore();
    Settings oldSettings = store.get();
    Credentials credentials = new Credentials("trillian", "secret");
    when(checker.checkCredentials(credentials, DefaultSettingsStore.DEFAULT_UPDATE_WEBSITE)).thenReturn(true);
    Settings settings = new Settings(credentials, true, true, true);
    store.set(settings);
    Settings other = store.get();
    assertEquals(settings, other);
    verify(eventBus, times(1)).post(new SettingsChangedEvent(settings, oldSettings));
  }
  
  @Test(expected = CredentialsInvalidSettingsException.class)
  @SubjectAware(username = "trillian", password = "secret")
  public void testSetWithInvalidCredentials()
  {
    DefaultSettingsStore store = createSettingsStore();
    store.set(new Settings(new Credentials("dent", "secret"), true, true, true));
  }
  
  private DefaultSettingsStore createSettingsStore()
  {
    return new DefaultSettingsStore(eventBus, checker, configurarion);
  }
  
  @Rule
  public ShiroRule shiro = new ShiroRule();
  
  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  
}
