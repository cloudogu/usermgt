package de.triology.universeadm;

import com.google.common.collect.Sets;
import javax.servlet.ServletContext;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author mbehlendorf
 */
public class DevelopmentSecurityModule extends BaseSecurityModule {

  public DevelopmentSecurityModule(ServletContext servletContext) {
    super(servletContext);
  }

  @Override
  protected void configureRealm() {
    bindRealm().to(DummyRealm.class);
    
    // protect uris
    addFilterChain("/api/users**", filterConfig(AUTHC_BASIC), filterConfig(ROLES, Roles.ADMINISTRATOR));
    addFilterChain("/api/groups**", filterConfig(AUTHC_BASIC), filterConfig(ROLES, Roles.ADMINISTRATOR));
    addFilterChain("/**", filterConfig(AUTHC_BASIC));
  }
  
  public static class DummyRealm extends AuthorizingRealm{

    public DummyRealm() {
          setAuthenticationTokenClass(UsernamePasswordToken.class);
    }
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo(Sets.newHashSet(Roles.ADMINISTRATOR));
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        return new SimpleAuthenticationInfo("admin", "admin", "dummy");
    }
   
  }

  
  
}
