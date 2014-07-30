/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.triology.universeadm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public abstract class AbstractLDAPManager<T> implements Manager<T>
{
  
  public static final String WILDCARD = "*";

  public static final String EQUAL = "=";
  
  private static final Logger logger = LoggerFactory.getLogger(AbstractLDAPManager.class);
  
  @Override
  public PagedResultList<T> getAll(int start, int limit)
  {
    logger.debug("get paged entities, start={} and limit={}", start, limit);
    return Paginations.createPaging(getAll(), start, limit);
  }
  
  @Override
  public PagedResultList<T> search(String query, int start, int limit)
  {
    logger.debug("search paged entities, query={}, start={} and limit={}", query, start, limit);
    return Paginations.createPaging(search(query), start, limit);
  }

}
