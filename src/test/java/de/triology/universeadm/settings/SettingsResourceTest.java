/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import de.triology.universeadm.Resources;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletResponse;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ssdorra
 */
public class SettingsResourceTest
{
  
  @Test
  public void testGetSettings() throws URISyntaxException, IOException
  {
    Settings setting = new Settings(null, false, true, true);
    SettingsStore store = mock(SettingsStore.class);
    when(store.get()).thenReturn(setting);
    MockHttpRequest request = MockHttpRequest.get("/settings");
    MockHttpResponse response = Resources.dispatch(new SettingsResource(store), request);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    Settings rs = Resources.parseJson(response, Settings.class);
    assertEquals(setting, rs);
  }
  
  @Test
  public void testUpdateSettings() throws URISyntaxException, IOException
  {
    Settings setting = new Settings(null, false, true, true);
    SettingsStore store = mock(SettingsStore.class);
    MockHttpRequest request = MockHttpRequest.get("/settings");
    MockHttpResponse response = Resources.dispatch(new SettingsResource(store), request, setting);
    assertEquals(HttpServletResponse.SC_NO_CONTENT, response.getStatus());
  }
  
}
