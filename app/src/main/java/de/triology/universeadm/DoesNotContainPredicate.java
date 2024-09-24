package de.triology.universeadm;

import com.google.common.base.Predicate;
import java.util.Collection;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
