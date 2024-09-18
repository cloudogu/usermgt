package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//~--- JDK imports ------------------------------------------------------------

import java.util.Locale;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public enum Stage
{
  DEVELOPMENT, PRODUCTION;

  /** Field description */
  private static final String ENV_STAGE = "UNIVERSEADM_STAGE";

  /** Field description */
  private static final String PROPERTY_STAGE = "universeadm.stage";

  /** Field description */
  private static final Stage current;

  /** Field description */
  private static final Logger logger = LoggerFactory.getLogger(Stage.class);

  //~--- static initializers --------------------------------------------------

  static
  {
    String stage = System.getenv(ENV_STAGE);

    if (Strings.isNullOrEmpty(stage))
    {
      stage = System.getProperty(PROPERTY_STAGE);
    }

    if (Strings.isNullOrEmpty(stage))
    {
      current = Stage.PRODUCTION;
    }
    else
    {
      current = Stage.valueOf(stage.toUpperCase(Locale.ENGLISH));
    }
    
    logger.info("start with stage {}", stage);
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Returns the current stage of universeadm.
   *
   *
   * @return current stage
   */
  public static Stage get()
  {
    return current;
  }
}
