/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author ssdorra
 * @param <T>
 */
@XmlRootElement(name = "paged-result")
@XmlAccessorType(XmlAccessType.FIELD)
public class PagedResultList<T>
{

  @XmlElement(name = "entry")
  @XmlElementWrapper(name = "entries")
  private final List<T> entries;
  private final int start;
  private final int limit;
  private final int totalEntries;

  public PagedResultList(List<T> entries, int start, int limit, int totalEntries)
  {
    this.entries = entries;
    this.start = start;
    this.limit = limit;
    this.totalEntries = totalEntries;
  }

  public List<T> getEntries()
  {
    return entries;
  }

  public int getStart()
  {
    return start;
  }

  public int getLimit()
  {
    return limit;
  }

  public int getTotalEntries()
  {
    return totalEntries;
  }

}
