package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Authentication filter for api authentication. The filter is similar to the
 * {@link BasicHttpAuthenticationFilter}, but it will only send the basic
 * authentication challenge if the client is not a browser. In case of a browser
 * the filter will issue a redirect to the login page.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @since 1.2.0
 */
public final class ApiAuthenticationFilter extends BasicHttpAuthenticationFilter
{

  /** user-agent http header */
  @VisibleForTesting
  static final String HEADER_USERAGENT = "User-Agent";

  /** user-agent value to detect a browser */
  private static final String UA_BROWSER = "mozilla";

  //~--- methods --------------------------------------------------------------

  /**
   * Sends basic authentication challenge, if the client is no browser. In the
   * case of a browser the method will issue an redirect to the login page.
   *
   * @param req http request
   * @param res http response
   *
   * @return
   */
  @Override
  protected boolean sendChallenge(ServletRequest req, ServletResponse res)
  {
    HttpServletRequest request = WebUtils.toHttp(req);

    if (isBrowser(request))
    {
      redirect(req, res);
    }
    else
    {
      super.sendChallenge(req, res);
    }

    return false;
  }

  /**
   * Returns {@code true} if the value contains the check.
   *
   *
   * @param v value
   * @param c check
   *
   * @return {@code true} if the value contains the check
   */
  private boolean contains(String v, String c)
  {
    return Strings.nullToEmpty(v).toLowerCase(Locale.ENGLISH).contains(c);
  }

  private void redirect(ServletRequest req, ServletResponse res)
  {
    try
    {
      saveRequestAndRedirectToLogin(req, res);
    }
    catch (IOException ex)
    {
      throw Throwables.propagate(ex);
    }
  }

  //~--- get methods ----------------------------------------------------------

  private boolean isBrowser(HttpServletRequest request)
  {
    String header = request.getHeader(HEADER_USERAGENT);

    return contains(header, UA_BROWSER);
  }
}
