package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.annotations.VisibleForTesting;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class PathXmlAdapter extends XmlAdapter<String, File>
{

  /** Field description */
  private static final String BASEDIR = "{basedir}";

  //~--- constructors ---------------------------------------------------------

  /**
   * Constructs ...
   *
   */
  public PathXmlAdapter()
  {
    this(BaseDirectory.get());
  }

  /**
   * Constructs ...
   *
   *
   * @param baseDirectory
   */
  @VisibleForTesting
  PathXmlAdapter(File baseDirectory)
  {
    this.baseDirectory = baseDirectory.getAbsolutePath();
  }

  /**
   * Constructs ...
   *
   *
   * @param baseDirectory
   */
  @VisibleForTesting
  PathXmlAdapter(String baseDirectory)
  {
    this.baseDirectory = baseDirectory;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param v
   *
   * @return
   */
  @Override
  public String marshal(File v)
  {
    return v.getAbsolutePath();
  }

  /**
   * Method description
   *
   *
   * @param v
   *
   * @return
   */
  @Override
  public File unmarshal(String v)
  {
    return new File(v.replace(BASEDIR, baseDirectory));
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final String baseDirectory;
}
