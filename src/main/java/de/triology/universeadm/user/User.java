/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;

/**
 *
 * @author Sebastian Sdorra
 */
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
   * @param displayName
   * @param givenname
   * @param surname
   * @param mail
   * @param password
   * @param memberOf
   */
  public User(String username, String displayName, String givenname,
              String surname, String mail, String password, List<String> memberOf)
  {
    this.username = username;
    this.displayName = displayName;
    this.givenname = givenname;
    this.surname = surname;
    this.mail = mail;
    this.password = password;
    this.memberOf = memberOf;
  }

  //~--- get methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @return
   */
  public String getDisplayName()
  {
    return displayName;
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
  public void setDisplayName(String commonname)
  {
    this.displayName = commonname;
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

  public void setMemberOf(List<String> memberOf)
  {
    this.memberOf = memberOf;
  }

  public List<String> getMemberOf()
  {
    if ( memberOf == null ){
      memberOf = Lists.newArrayList();
    }
    return memberOf;
  }
  
  

  @Override
  public int compareTo(User o)
  {
    return Strings.nullToEmpty(username).compareTo(Strings.nullToEmpty(o.username));
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(username, displayName, givenname, surname, mail, memberOf);
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
            && Objects.equal(displayName, other.displayName)
            && Objects.equal(givenname, other.givenname)
            && Objects.equal(surname, other.surname)
            && Objects.equal(mail, other.mail)
            && Objects.equal(memberOf, other.memberOf);
  }

  @Override
  public String toString()
  {
    return Objects.toStringHelper(this)
            .add("username", username)
            .add("displayName", displayName)
            .add("givenname", givenname)
            .add("surname", surname)
            .add("mail", mail)
            .add("memberOf", memberOf)
            .toString();
  }

  //~--- fields ---------------------------------------------------------------
  /**
   * Field description
   */
  @NotNull
  @Size(min = 1)
  private String displayName;

  /**
   * Field description
   */
  private String givenname;

  /**
   * Field description
   */
  @Email
  @NotNull
  private String mail;

  /**
   * Field description
   */
  private String password;

  /**
   * Field description
   */
  @NotNull
  @Size(min = 1)
  private String surname;

  /**
   * Field description
   */
  @NotNull
  @Size(min = 1)
  private String username;
  
  /**
   * Field description
   */
  private List<String> memberOf;
}
