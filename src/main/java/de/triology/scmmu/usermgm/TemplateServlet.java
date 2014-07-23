/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.scmmu.usermgm;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Resources;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.concurrent.TimeUnit;
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
      cache = CacheBuilder.newBuilder().maximumSize(5).expireAfterWrite(1l, TimeUnit.HOURS).build();
    }
    else
    {
      logger.info("create template servlet with disabled cache");
      cache = CacheBuilder.newBuilder().maximumSize(0).build();
    }
  }

  private static final Logger logger = LoggerFactory.getLogger(TemplateServlet.class);

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    String contextPath = req.getContextPath();
    String path = req.getRequestURI().substring(contextPath.length());
    String value = cache.getIfPresent(path);
    if (value == null)
    {
      URL resource = getServletContext().getResource(path);
      if (resource != null)
      {
        if (contextPath.endsWith("/"))
        {
          contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        logger.trace("found resource {} at {}", path, resource);
        value = Resources.toString(resource, Charsets.UTF_8);
        value = value.replaceAll("\\$\\{contextPath\\}", contextPath);
        cache.put(path, value);
      }
      else
      {
        logger.debug("could not find resource {}", path);
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
      }
    }
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

}
