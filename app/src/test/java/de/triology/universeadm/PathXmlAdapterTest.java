package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class PathXmlAdapterTest
{

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Test
  public void testMarshall() throws IOException
  {
    File base = folder.newFolder();
    PathXmlAdapter adapter = new PathXmlAdapter(base);

    assertEquals(base.getAbsolutePath(), adapter.marshal(base));
  }

  /**
   * Method description
   *
   *
   * @throws IOException
   */
  @Test
  public void testUnmarshall() throws IOException
  {
    File base = folder.newFolder();
    PathXmlAdapter adapter = new PathXmlAdapter(base);

    assertEquals(new File(base, "test"), adapter.unmarshal("{basedir}/test"));
    assertEquals("/test", "/test");
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();
}
