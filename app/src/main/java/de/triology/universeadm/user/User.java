/*
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.scm-manager.com
 */



package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.triology.universeadm.validation.RDN;

import org.hibernate.validator.constraints.Email;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class User implements Comparable<User>
{

  /**
   * Constructs ...
   *
   */
  public User() {}

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
    String surname, String mail, String password, boolean pwdReset, List<String> memberOf)
  {
    this.username = username;
    this.displayName = displayName;
    this.givenname = givenname;
    this.surname = surname;
    this.mail = mail;
    this.password = password;
    this.pwdReset = pwdReset;
    this.external = false;
    this.memberOf = memberOf;
  }

  public User(String username, String displayName, String givenname,
              String surname, String mail, String password, boolean pwdReset, List<String> memberOf, boolean external) {

    this(username, displayName, givenname, surname, mail, password, pwdReset, memberOf);
    this.external = external;
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
  public int compareTo(User o)
  {
    return Strings.nullToEmpty(username).toLowerCase().compareTo(
      Strings.nullToEmpty(o.username).toLowerCase());
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

    final User other = (User) obj;

    return Objects.equal(username, other.username)
      && Objects.equal(displayName, other.displayName)
      && Objects.equal(givenname, other.givenname)
      && Objects.equal(surname, other.surname)
      && Objects.equal(mail, other.mail)
      && Objects.equal(memberOf, other.memberOf)
      && Objects.equal(pwdReset, other.pwdReset)
      && Objects.equal(external, other.external);
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
    return Objects.hashCode(username, displayName, givenname, surname, mail,
      memberOf, pwdReset, external);
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
    return MoreObjects.toStringHelper(this)
            .add("username", username)
            .add("displayName", displayName)
            .add("givenname", givenname)
            .add("surname", surname)
            .add("mail", mail)
            .add("memberOf", memberOf)
            .add("pwdReset", pwdReset)
            .add("external", external)
            .toString();
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
  public List<String> getMemberOf()
  {
    if (memberOf == null)
    {
      memberOf = Lists.newArrayList();
    }

    return memberOf;
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
  public boolean isPwdReset() {
    return pwdReset;
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
   * @param memberOf
   */
  public void setMemberOf(List<String> memberOf)
  {
    this.memberOf = memberOf;
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
   * @param pwdReset
   */
  public void setPwdReset(boolean pwdReset) {
    this.pwdReset = pwdReset;
  }

  public void setExternal(boolean external) {
    this.external = external;
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

  //~--- fields ---------------------------------------------------------------

  /**
   * Field description
   */
  @NotBlank
  private String displayName;

  /**
   * Field description
   */
  @NotBlank
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
  private List<String> memberOf;

  /**
   * Field description
   */
  private String password;


  /**
   * Field description
   */
  private boolean pwdReset;

  public boolean isExternal() {
    return external;
  }

  /**
   * Field description
   */
  private boolean external;

  /**
   * Field description
   */
  @NotBlank
  private String surname;

  /**
   * Field description
   */
  @RDN
  private String username;
}
