/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.settings;

import com.github.legman.EventBus;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.triology.universeadm.BaseDirectory;
import de.triology.universeadm.Roles;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public class DefaultSettingsStore implements SettingsStore
{

  private static final String FLAG_TRUE = "1";

  private static final String FLAG_FALSE = "0";

  private static final String FLAG_VALID = "valid";

  private static final String FLAG_INVALID = "invalid";

  private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

  private static final Charset UTF8 = Charsets.UTF_8;

  private static final String FILENAME = "settings.xml";
  
  private static final String DEFAULT_UPDATE_WEBSITE = "https://www.scm-manager.com/applupdateservice/applupdate.php";

  private static final Logger logger = LoggerFactory.getLogger(DefaultSettingsStore.class);
  
  private final EventBus eventBus;
  private final CredentialsChecker checker;
  private final SettingsStoreConfiguration config;

  @Inject
  public DefaultSettingsStore(EventBus eventBus, CredentialsChecker checker)
  {
    this(eventBus, checker, JAXB.unmarshal(BaseDirectory.get(FILENAME), SettingsStoreConfiguration.class));
  }

  public DefaultSettingsStore(EventBus eventBus, CredentialsChecker checker, SettingsStoreConfiguration config)
  {
    this.eventBus = eventBus;
    this.checker = checker;
    this.config = config;
  }

  @Override
  public void set(Settings settings)
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    Credentials credentials = settings.getUpdateServiceCredentials();
    if ( credentials != null && credentials.isValid() && ! validateCredentials(credentials) )
    {
      throw new SettingsException("credentials are not valid");
    }
    Settings oldSettings = get();
    writeFlagFile(config.getUpdateBugzillaPluginFile(), settings.isUpdateBugzillaPlugin());
    writeFlagFile(config.getUpdateCasServerFile(), settings.isUpdateCasServer());
    writeFlagFile(config.getUpdateCheckEnabledFile(), settings.isUpdateCheckEnabled());
    writeCredentialsFile(config.getUpdateServiceCredentialsFile(), settings.getUpdateServiceCredentials());
    eventBus.post(new SettingsChangedEvent(settings, oldSettings));
  }

  @Override
  public Settings get()
  {
    SecurityUtils.getSubject().checkRole(Roles.ADMINISTRATOR);
    Settings settings = new Settings(
      readCredentialsFile(config.getUpdateServiceCredentialsFile()),
      readFlagFile(config.getUpdateCheckEnabledFile(), true),
      readFlagFile(config.getUpdateBugzillaPluginFile(), true),
      readFlagFile(config.getUpdateCasServerFile(), true)
    );
    return settings;
  }

  @Override
  public boolean validateCredentials(Credentials credentials)
  {
    String website = getUpdateWebsite();
    logger.info("check credentials against {}", website);
    
    try {
      return checker.checkCredentials(credentials, website);
    } catch (IOException ex){
      throw new SettingsException("creadential validation failed", ex);
    }
  }
  
  private String getUpdateWebsite()
  {
    return readFile(config.getUpdateWebsiteFile(), DEFAULT_UPDATE_WEBSITE);
  }
  
  private String readFile(File file, String defaultValue){
    String result = defaultValue;
    if (file.exists())
    {
      try
      {
        String content = Files.toString(file, UTF8);
        if (!Strings.isNullOrEmpty(content))
        {
          result = content;
        }
      }
      catch (IOException ex)
      {
        throw new SettingsException("could not read file", ex);
      }
    }
    return result;
  }

  private Credentials readCredentialsFile(File file)
  {
    Credentials credentials = null;
    if (file.exists())
    {
      try
      {
        List<String> content = Files.readLines(file, UTF8);
        if (content != null && !content.isEmpty())
        {
          String last = Iterables.getLast(content);
          if (FLAG_VALID.equals(last))
          {
            credentials = new Credentials(content.get(0), content.get(1));
          }
        }
      }
      catch (IOException ex)
      {
        throw new SettingsException("could not read credentials file", ex);
      }
    }
    return credentials;
  }

  private void writeCredentialsFile(File file, Credentials credentials)
  {
    StringBuilder content = new StringBuilder();
    if (credentials.isValid())
    {
      content.append(credentials.getUsername()).append(LINE_SEPARATOR);
      content.append(credentials.getPassword()).append(LINE_SEPARATOR);
      content.append(FLAG_VALID).append(LINE_SEPARATOR);
    }
    else
    {
      content.append(LINE_SEPARATOR).append(LINE_SEPARATOR).append(FLAG_INVALID);
    }
    try
    {
      Files.write(content, file, UTF8);
    }
    catch (IOException ex)
    {
      throw new SettingsException("could not store credentials file", ex);
    }
  }

  private boolean readFlagFile(File file, boolean defaultState)
  {
    boolean result = defaultState;
    if (file.exists())
    {
      try
      {
        String content = Files.toString(file, UTF8);
        if (!Strings.isNullOrEmpty(content))
        {
          result = content.trim().equals(FLAG_TRUE);
        }
      }
      catch (IOException ex)
      {
        throw new SettingsException("could not read flag file", ex);
      }
    }
    return result;
  }

  private void writeFlagFile(File file, boolean value)
  {
    try
    {
      Files.write(val(value), file, UTF8);
    }
    catch (IOException ex)
    {
      throw new SettingsException("could not store flag file", ex);
    }
  }

  private String val(boolean v)
  {
    return v ? FLAG_TRUE : FLAG_FALSE;
  }

  @XmlRootElement(name = "settings")
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class SettingsStoreConfiguration
  {

    private static final String DEFAULT_UPDATE_SERVICE_CREDENTIALS_FILE = "scmcreds";

    private static final String DEFAULT_UPDATE_BUGZILLA_PLUGIN_FILE = "scmbugplug";

    private static final String DEFAULT_UPDATE_CAS_SERVER_FILE = "scmcasupdt";

    private static final String DEFAULT_UPDATE_CHECK_ENABLED_FILE = "scmupdcheck";
    
    private static final String DEFAULT_UPDATE_WEBSITE_FILE = "updateserver";

    @XmlElement(name = "configuration-directory")
    private File configurationDirectory;

    @XmlElement(name = "update-bugzilla-plugin-file")
    private File updateBugzillaPluginFile;

    @XmlElement(name = "update-cas-server-file")
    private File updateCasServerFile;

    @XmlElement(name = "update-service-credentials-file")
    private File updateServiceCredentialsFile;

    @XmlElement(name = "update-check-enabled-file")
    private File updateCheckEnabledFile;
    
    @XmlElement(name = "update-website-file")
    private File updateWebsiteFile;

    public SettingsStoreConfiguration()
    {
    }

    public SettingsStoreConfiguration(File configurationDirectory)
    {
      this.configurationDirectory = configurationDirectory;
    }

    public File getUpdateCheckEnabledFile()
    {
      return getFile(updateCheckEnabledFile, DEFAULT_UPDATE_CHECK_ENABLED_FILE);
    }

    public File getUpdateBugzillaPluginFile()
    {
      return getFile(updateBugzillaPluginFile, DEFAULT_UPDATE_BUGZILLA_PLUGIN_FILE);
    }

    public File getUpdateCasServerFile()
    {
      return getFile(updateCasServerFile, DEFAULT_UPDATE_CAS_SERVER_FILE);
    }

    public File getUpdateServiceCredentialsFile()
    {
      return getFile(updateServiceCredentialsFile, DEFAULT_UPDATE_SERVICE_CREDENTIALS_FILE);
    }
    
    public File getUpdateWebsiteFile()
    {
      return getFile(updateWebsiteFile, DEFAULT_UPDATE_WEBSITE_FILE);
    }

    private File getFile(File file, String defaultFile)
    {
      if (file == null && configurationDirectory == null)
      {
        throw new SettingsException("there is not configuration for this file");
      }
      else if (file == null)
      {
        file = new File(configurationDirectory, defaultFile);
      }
      return file;
    }

  }

}
