/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



package de.triology.scmmu.usermgm.user;

//~--- JDK imports ------------------------------------------------------------

import de.triology.scmmu.usermgm.PagedResultList;
import java.util.List;

/**
 *
 * @author Sebastian Sdorra
 */
public interface UserManager
{

  /**
   * Method description
   *
   *
   * @param user
   */
  public void create(User user);

  /**
   * Method description
   *
   *
   * @param user
   */
  public void modify(User user);

  /**
   * Method description
   *
   *
   * @param user
   */
  public void remove(User user);

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param username
   *
   * @return
   */
  public User get(String username);

  /**
   * Method description
   *
   *
   * @return
   */
  public List<User> getAll();
  
  public List<User> search(String query);
  
  public PagedResultList<User> search(String query, int start, int limit);
  
  public PagedResultList<User> getAll(int start, int limit);
}
