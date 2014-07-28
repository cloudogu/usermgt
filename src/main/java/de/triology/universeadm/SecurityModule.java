/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
public class SecurityModule extends ShiroWebModule
{

  private static final String CAS_VALIDATION_PROTOCOL = "shiro.validationProtocol";
  private static final String CAS_VALIDATION_PROTOCOL_VALUE = "SAML";
  private static final String CAS_SERVER_URL = "shiro.casServerUrlPrefix";
  private static final String CAS_LOGIN_URL = "shiro.loginUrl";
  private static final String CAS_SERVICE = "shiro.casService";

  private static final Logger logger = LoggerFactory.getLogger(SecurityModule.class);

  public SecurityModule(ServletContext context)
  {
    super(context);
  }

  private void config(String key, String value)
  {
    logger.debug("bind config {} to {}", key, value);
    bindConstant().annotatedWith(Names.named(key)).to(value);
  }

  private CasConfiguration getCasConfiguration()
  {
    CasConfiguration casConfiguration = BaseDirectory.getConfiguration(CasConfiguration.FILE, CasConfiguration.class);
    if (casConfiguration == null)
    {
      throw new IllegalStateException("could not load cas configuration");
    }
    return casConfiguration;
  }

  @Override
  protected void configureShiroWeb()
  {
    CasConfiguration cas = getCasConfiguration();
    bind(CasConfiguration.class).toInstance(cas);
    expose(CasConfiguration.class);

    config(CAS_SERVER_URL, cas.getServerUrl());
    config(CAS_LOGIN_URL, cas.getLoginUrl());
    config(CAS_SERVICE, cas.getService());
    config(CAS_VALIDATION_PROTOCOL, CAS_VALIDATION_PROTOCOL_VALUE);

    // use provider to configure realm, 
    // beacuse it looks like guice does not set constants for multi binding
    bindRealm().toProvider(CasRealmProvider.class).in(Singleton.class);
    bind(SubjectFactory.class).to(CasSubjectFactory.class);

    addFilterChain("/login/cas", ANON, Key.get(CasFilter.class));
    addFilterChain("/api/users**", config(ROLES, "admins"));
    addFilterChain("/api/groups**", config(ROLES, "admins"));
    addFilterChain("/**", AUTHC);
  }

  private static class CasRealmProvider implements Provider<Realm>
  {

    private final CasConfiguration configuration;

    @Inject
    public CasRealmProvider(CasConfiguration configuration)
    {
      this.configuration = configuration;
    }

    @Override
    public Realm get()
    {
      return new FixedCasRealm(configuration);
    }

  }

  /**
   * CasRealm with fixed support for multi value attributes
   * 
   * @see https://issues.apache.org/jira/browse/SHIRO-442
   */
  public static class FixedCasRealm extends CasRealm
  {
    
    private final CasConfiguration configuration;

    public FixedCasRealm(CasConfiguration configuration)
    {
      this.configuration = configuration;
      this.setCasServerUrlPrefix(configuration.getServerUrl());
      this.setCasService(configuration.getService());
      this.setRoleAttributeNames(configuration.getRoleAttributeNames());
      this.setValidationProtocol(CAS_VALIDATION_PROTOCOL_VALUE);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals)
    {
      // retrieve user information
      SimplePrincipalCollection principalCollection = (SimplePrincipalCollection) principals;
      List<Object> listPrincipals = principalCollection.asList();
      Map<String, String> attributes = (Map<String, String>) listPrincipals.get(1);
      // create simple authorization info
      SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
      // add default roles
      addRoles(simpleAuthorizationInfo, split(getDefaultRoles()));
      // add default permissions
      addPermissions(simpleAuthorizationInfo, split(getDefaultPermissions()));
      // get roles from attributes
      List<String> attributeNames = split(getRoleAttributeNames());
      for (String attributeName : attributeNames)
      {
        final Object value = attributes.get(attributeName);
        if (value instanceof Collection<?>)
        {
          for (final Object valueEntry : (Collection<?>) value)
          {
            addRoles(simpleAuthorizationInfo, split((String) valueEntry));
          }
        }
        else
        {
          addRoles(simpleAuthorizationInfo, split((String) value));
        }
      }
      // get permissions from attributes
      attributeNames = split(getPermissionAttributeNames());
      for (String attributeName : attributeNames)
      {
        final Object value = attributes.get(attributeName);
        if (value instanceof Collection<?>)
        {
          for (final Object valueEntry : (Collection<?>) value)
          {
            addPermissions(simpleAuthorizationInfo, split((String) valueEntry));
          }
        }
        else
        {
          addPermissions(simpleAuthorizationInfo, split((String) value));
        }
      }
      
      if (simpleAuthorizationInfo.getRoles().contains(configuration.getAdministratorRole()))
      {
        simpleAuthorizationInfo.addRole(Roles.ADMINISTRATOR);
      }
      
      return simpleAuthorizationInfo;
    }

    /**
     * Add roles to the simple authorization info.
     *
     * @param simpleAuthorizationInfo
     * @param roles the list of roles to add
     */
    private void addRoles(SimpleAuthorizationInfo simpleAuthorizationInfo, List<String> roles)
    {
      for (String role : roles)
      {
        simpleAuthorizationInfo.addRole(role);
      }
    }

    /**
     * Add permissions to the simple authorization info.
     *
     * @param simpleAuthorizationInfo
     * @param permissions the list of permissions to add
     */
    private void addPermissions(SimpleAuthorizationInfo simpleAuthorizationInfo, List<String> permissions)
    {
      for (String permission : permissions)
      {
        simpleAuthorizationInfo.addStringPermission(permission);
      }
    }

    /**
     * Split a string into a list of not empty and trimmed strings, delimiter is
     * a comma.
     *
     * @param s the input string
     * @return the list of not empty and trimmed strings
     */
    private List<String> split(String s)
    {
      List<String> list = new ArrayList<>();
      String[] elements = StringUtils.split(s, ',');
      if (elements != null && elements.length > 0)
      {
        for (String element : elements)
        {
          if (StringUtils.hasText(element))
          {
            list.add(element.trim());
          }
        }
      }
      return list;
    }

  }

}
