/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import javax.xml.bind.JAXB;

/**
 *
 * @author Sebastian Sdorra
 */
public final class BaseDirectory
{

  /** Field description */
  private static final String ENV_BASEDIR = "UNIVERSEADM_HOME";

  /** Field description */
  private static final String PROPERTY_BASEDIR = "universeadm.home";

  /** Field description */
  private static final File baseDirectory;

  /**
   * the logger for BaseDirectory
   */
  private static final Logger logger =
    LoggerFactory.getLogger(BaseDirectory.class);

  //~--- static initializers --------------------------------------------------

  static
  {
    String basedir = System.getenv(ENV_BASEDIR);

    if (Strings.isNullOrEmpty(basedir))
    {
      basedir = System.getProperty(PROPERTY_BASEDIR);

      if (Strings.isNullOrEmpty(basedir))
      {
        basedir = new File(System.getProperty("user.home"),
          ".universeadm").getAbsolutePath();
      }
    }

    logger.info("start with basedir at {}", basedir);
    baseDirectory = new File(basedir);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public static File get()
  {
    return baseDirectory;
  }

  /**
   * Method description
   *
   *
   * @param name
   *
   * @return
   */
  public static File get(String name)
  {
    return new File(baseDirectory, name);
  }

  /**
   * Method description
   *
   *
   * @param name
   * @param type
   * @param <T>
   *
   * @return
   */
  public static <T> T getConfiguration(String name, Class<T> type)
  {
    return JAXB.unmarshal(get(name), type);
  }
}
