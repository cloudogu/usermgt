/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm.settings;

import com.google.common.base.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ssdorra
 */
@XmlRootElement(name = "credentials")
@XmlAccessorType(XmlAccessType.FIELD)
public class Credentials
{
  private String username;
 
  private String password;

  public Credentials()
  {
  }

  public Credentials(String username, String password)
  {
    this.username = username;
    this.password = password;
  }

  public String getPassword()
  {
    return password;
  }

  public String getUsername()
  {
    return username;
  }

  @Override
  public int hashCode()
  {
    return Objects.hashCode(username, password);
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
    final Credentials other = (Credentials) obj;
    return Objects.equal(username, other.username) 
      && Objects.equal(password, other.password);
  }
  
  @Override
  public String toString()
  {
    String pwd = password != null ? "(is set)" : "(not set)";
    return Objects.toStringHelper(this)
                  .add("username", username)
                  .add("password", pwd)
                  .toString();
  }
  
}
