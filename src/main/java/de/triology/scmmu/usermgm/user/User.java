/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.scmmu.usermgm.user;

//~--- non-JDK imports --------------------------------------------------------
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.unboundid.ldap.sdk.persist.LDAPField;
import com.unboundid.ldap.sdk.persist.LDAPObject;

/**
 *
 * @author Sebastian Sdorra
 */
@LDAPObject(structuralClass = "organizationalperson",
        superiorClass =
        {
          "inetorgperson", "person", "top"
        })
public class User implements Comparable<User>
{

  /**
   * Constructs ...
   *
   */
  public User()
  {
  }

  /**
   * Constructs ...
   *
   *
   * @param username
   */
  public User(String username)
  {
    this.username = username;
  }

  /**
   * Constructs ...
   *
   *
   * @param username
   * @param commonname
   * @param givenname
   * @param surname
   * @param mail
   * @param password
   */
  public User(String username, String commonname, String givenname,
          String surname, String mail, String password)
  {
    this.username = username;
    this.commonname = commonname;
    this.givenname = givenname;
    this.surname = surname;
    this.mail = mail;
    this.password = password;
  }

  //~--- get methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @return
   */
  public String getCommonname()
  {
    return commonname;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getGivenname()
  {
    return givenname;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getMail()
  {
    return mail;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getSurname()
  {
    return surname;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  public String getUsername()
  {
    return username;
  }

  //~--- set methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param commonname
   */
  public void setCommonname(String commonname)
  {
    this.commonname = commonname;
  }

  /**
   * Method description
   *
   *
   * @param givenname
   */
  public void setGivenname(String givenname)
  {
    this.givenname = givenname;
  }

  /**
   * Method description
   *
   *
   * @param mail
   */
  public void setMail(String mail)
  {
    this.mail = mail;
  }

  /**
   * Method description
   *
   *
   * @param password
   */
  public void setPassword(String password)
  {
    this.password = password;
  }

  /**
   * Method description
   *
   *
   * @param surname
   */
  public void setSurname(String surname)
  {
    this.surname = surname;
  }

  /**
   * Method description
   *
   *
   * @param username
   */
  public void setUsername(String username)
  {
    this.username = username;
  }

  @Override
  public int compareTo(User o)
  {
    return Strings.nullToEmpty(username).compareTo(Strings.nullToEmpty(o.username));
  }

 @Override
  public int hashCode()
  {
    return Objects.hashCode(username, commonname, givenname, surname, mail, password);
  }

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
    final User other = (User) obj;
    return Objects.equal(username, other.username)
            && Objects.equal(commonname, other.commonname)
            && Objects.equal(givenname, other.givenname)
            && Objects.equal(surname, other.surname)
            && Objects.equal(mail, other.mail);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
            .add("username", username)
            .add("commonname", commonname)
            .add("givenname", givenname)
            .add("surname", surname)
            .add("mail", mail)
            .toString();
  }
  
  //~--- fields ---------------------------------------------------------------
  /**
   * Field description
   */
  @LDAPField(attribute = "cn")
  private String commonname;

  /**
   * Field description
   */
  @LDAPField(attribute = "givenName")
  private String givenname;

  /**
   * Field description
   */
  @LDAPField(attribute = "mail")
  private String mail;

  /**
   * Field description
   */
  private String password;

  /**
   * Field description
   */
  @LDAPField(attribute = "sn")
  private String surname;

  /**
   * Field description
   */
  @LDAPField(attribute = "uid", inRDN = true)
  private String username;
}
