/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.triology.universeadm;

import com.google.common.base.Predicate;
import java.util.Collection;

/**
 *
 * @author ssdorra
 * @param <T>
 */
public class DoesNotContainPredicate<T> implements Predicate<T>
{

  private final Collection<T> collection;

  public DoesNotContainPredicate(Collection<T> collection)
  {
    this.collection = collection;
  }

  @Override
  public boolean apply(T input)
  {
    return !collection.contains(input);
  }

}
