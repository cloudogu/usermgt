/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.io.Resources;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 */
@Singleton
public class TemplateServlet extends HttpServlet
{

  private final Cache<String, String> cache;

  private static final boolean CACHE_DISABLED = Boolean.getBoolean(TemplateServlet.class.getName().concat(".disable-cache"));

  public TemplateServlet()
  {
    if (!CACHE_DISABLED)
    {
      logger.info("create template servlet with enabled cache");
      cache = Caches.createSmallCache();
    }
    else
    {
      logger.info("create template servlet with disabled cache");
      cache = Caches.createDisabledCache();
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(TemplateServlet.class);

  private String getNormalizePath(HttpServletRequest req)
  {
    String path = req.getRequestURI().substring(req.getContextPath().length());
    if (path.isEmpty())
    {
      path = "/index.html";
    }
    else if (path.endsWith("/"))
    {
      path = path.concat("index.html");
    }
    return path;
  }

  private String processResource(HttpServletRequest req, String path, URL resource) throws IOException
  {
    String contextPath = req.getContextPath();
    if (contextPath.endsWith("/"))
    {
      contextPath = contextPath.substring(0, contextPath.length() - 1);
    }
    logger.trace("found resource {} at {}", path, resource);
    String value = Resources.toString(resource, Charsets.UTF_8);
    value = value.replaceAll("\\$\\{contextPath\\}", contextPath);
    return value;
  }

  private void writeOutput(HttpServletResponse resp, String path, String value) throws IOException
  {
    if (!Strings.isNullOrEmpty(value))
    {
      try (PrintWriter writer = resp.getWriter())
      {
        writer.println(value);
      }
    }
    else
    {
      logger.warn("{} returned without content", path);
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    String path = getNormalizePath(req);
    String value = cache.getIfPresent(path);
    if (value == null)
    {
      URL resource = getServletContext().getResource(path);
      if (resource != null)
      {
        value = processResource(req, path, resource);
        cache.put(path, value);
      }
      else
      {
        logger.debug("could not find resource {}", path);
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
    else
    {
      logger.trace("return chached value for {}", path);
    }

    writeOutput(resp, path, value);
  }

}
