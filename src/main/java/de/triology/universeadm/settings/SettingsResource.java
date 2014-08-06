/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.settings;

import com.google.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ssdorra
 */
@Path("settings")
public class SettingsResource
{

  private final SettingsStore store;

  @Inject
  public SettingsResource(SettingsStore store)
  {
    this.store = store;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public void updateSettings(Settings settings)
  {
    this.store.set(settings);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Settings getSettings()
  {
    return store.get();
  }

}
