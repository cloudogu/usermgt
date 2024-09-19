package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;
import com.google.inject.Singleton;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Singleton
public class RedirectServlet extends HttpServlet
{

  /** Field description */
  public static final String PARAM_PATH = "redirect.path";

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @throws ServletException
   */
  @Override
  public void init() throws ServletException
  {
    redirectPath = getServletConfig().getInitParameter(PARAM_PATH);
    
    if (Strings.isNullOrEmpty(redirectPath))
    {
      throw new ServletException("init-param for redirect.path is not defined");
    }
  }

  /**
   * Method description
   *
   *
   * @param req
   * @param resp
   *
   * @throws IOException
   * @throws ServletException
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException
  {
    resp.sendRedirect(req.getContextPath() + redirectPath);
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String redirectPath;
}
