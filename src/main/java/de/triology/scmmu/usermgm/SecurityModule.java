/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.scmmu.usermgm;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import javax.servlet.ServletContext;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
public class SecurityModule extends ShiroWebModule
{
  
  private static final String CAS_SERVER_URL = "shiro.casServerUrlPrefix";
  private static final String CAS_LOGIN_URL = "shiro.loginUrl";
  private static final String CAS_SERVICE = "shiro.casService";
  
  private static final Logger logger = LoggerFactory.getLogger(SecurityModule.class);

  public SecurityModule(ServletContext context)
  {
    super(context);
  }

  private void config(String key, String value){
    logger.debug("bind config {} to {}", key, value);
    bindConstant().annotatedWith(Names.named(key)).to(value);
  }
  
  private CasConfiguration getCasConfiguration(){
    CasConfiguration casConfiguration = BaseDirectory.getConfiguration(CasConfiguration.FILE, CasConfiguration.class);
    if ( casConfiguration == null ){
      throw new IllegalStateException("could not load cas configuration");
    }
    return casConfiguration;
  }
  
  
  @Override
  protected void configureShiroWeb()
  {
    bindGuiceFilter(binder());
    CasConfiguration cas = getCasConfiguration();
    bind(CasConfiguration.class).toInstance(cas);
    config(CAS_SERVER_URL, cas.getServerUrl());
    config(CAS_LOGIN_URL, cas.getLoginUrl());
    config(CAS_SERVICE, cas.getService());
    
    // use provider to configure realm, 
    // beacuse it looks like guice does not set constants for multi binding
    bindRealm().toProvider(CasRealmProvider.class).in(Singleton.class);
    bind(SubjectFactory.class).to(CasSubjectFactory.class);
    
    addFilterChain("/login/cas", ANON, Key.get(CasFilter.class));
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
      CasRealm realm = new CasRealm();
      realm.setCasServerUrlPrefix(configuration.getServerUrl());
      realm.setCasService(configuration.getService());
      return realm;
    }
    
  }
  
}
