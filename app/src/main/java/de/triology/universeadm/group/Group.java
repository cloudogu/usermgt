package de.triology.universeadm.group;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.triology.universeadm.validation.RDN;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class Group implements Comparable<Group>
{

  /**
   * Constructs ...
   *
   */
  public Group() {}

  /**
   * Constructs ...
   *
   *
   * @param name
   */
  public Group(String name)
  {
    this.name = name;
  }

  /**
   * Constructs ...
   *
   *
   * @param name
   * @param description
   * @param members
   */
  public Group(String name, String description, List<String> members)
  {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  /**
   * Constructs ...
   *
   *
   * @param name
   * @param description
   * @param members
   */
  public Group(String name, String description, String... members)
  {
    this.name = name;
    this.description = description;
    this.members = Lists.newArrayList(members);
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param o
   *
   * @return
   */
  @Override
  public int compareTo(Group o)
  {
    return Strings.nullToEmpty(name).toLowerCase().compareTo(Strings.nullToEmpty(o.name).toLowerCase());
  }

  /**
   * Method description
   *
   *
   * @param obj
   *
   * @return
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    final Group other = (Group) obj;

    return Objects.equal(name, other.name)
      && Objects.equal(description, other.description)
      && Objects.equal(members, other.members);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public int hashCode()
  {
    return Objects.hashCode(name, description, members);
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public String toString()
  {
    //J-
    return MoreObjects.toStringHelper(this)
                      .add("name", name)
                      .add("description", description)
                      .add("members", members)
                      .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public List<String> getMembers()
  {
    if (members == null)
    {
      members = Lists.newArrayList();
    }

    return members;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getName()
  {
    return name;
  }

  //~--- set methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param description
   */
  public void setDescription(String description)
  {
    this.description = description;
  }

  /**
   * Method description
   *
   *
   * @param members
   */
  public void setMembers(List<String> members)
  {
    this.members = members;
  }

  /**
   * Method description
   *
   *
   * @param name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private String description;

  /** Field description */
  private List<String> members;

  /** Field description */
  @RDN
  private String name;
}
