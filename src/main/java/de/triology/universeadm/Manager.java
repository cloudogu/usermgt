/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import java.util.List;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public interface Manager<T>
{

  /**
   * Method description
   *
   *
   * @param object
   */
  public void create(T object);

  /**
   * Method description
   *
   *
   * @param object
   */
  public void modify(T object);

  /**
   * Method description
   *
   *
   * @param object
   */
  public void remove(T object);

  //~--- get methods ----------------------------------------------------------
  /**
   * Method description
   *
   *
   * @param objectname
   *
   * @return
   */
  public T get(String objectname);

  /**
   * Method description
   *
   *
   * @return
   */
  public List<T> getAll();

  /**
   * Method description
   *
   * @param query
   * 
   * @return
   */
  public List<T> search(String query);

  /**
   * Method description
   *
   * @param query
   * @param start
   * @param limit
   * 
   * @return
   */
  public PagedResultList<T> search(String query, int start, int limit);

  /**
   * Method description
   *
   * @param start
   * @param limit
   * 
   * @return
   */
  public PagedResultList<T> getAll(int start, int limit);
}
