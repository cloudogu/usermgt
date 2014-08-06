/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.settings;

import com.github.legman.EventBus;
import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.Roles;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author ssdorra
 */
@Singleton
public class DefaultSettingsStore implements SettingsStore
{

  private static final String FILENAME = "settings.xml";

  private final EventBus eventBus;
  private final File file;
  private final JAXBContext context;

  @Inject
  public DefaultSettingsStore(EventBus eventBus){
    this(eventBus, BaseDirectory.get(FILENAME));
  }
  
  public DefaultSettingsStore(EventBus eventBus, File file)
  {
    this.eventBus = eventBus;
    this.file = file;
    try
    {
      context = JAXBContext.newInstance(Settings.class);
    }
    catch (JAXBException ex)
    {
      // should never happen
      throw Throwables.propagate(ex);
    }
  }

  @Override
  public void set(Settings settings)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    Settings oldSettings = get();
    try
    {
      context.createMarshaller().marshal(settings, file);
      eventBus.post(new SettingsChangedEvent(settings, oldSettings));
    }
    catch (JAXBException ex)
    {
      throw Throwables.propagate(ex);
    }
  }

  @Override
  public Settings get()
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    Settings settings;
    try
    {
      if (!file.exists() || file.length() <= 0){
        settings = new Settings(null, true, true, true);
      } else {
        settings = (Settings) context.createUnmarshaller().unmarshal(file);
      }
    }
    catch (JAXBException ex)
    {
      throw Throwables.propagate(ex);
    }
    return settings;
  }

}
