package de.triology.universeadm;

//~--- non-JDK imports --------------------------------------------------------

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 * @param <T>
 */
public class AbstractEntityEvent<T> implements EntityEvent<T>
{

  /**
   * Constructs ...
   *
   *
   * @param entity
   * @param type
   */
  public AbstractEntityEvent(T entity, EventType type)
  {
    this.entity = entity;
    this.oldEntity = null;
    this.type = type;
  }

  /**
   * Constructs ...
   *
   *
   * @param entity
   * @param oldEntity
   */
  public AbstractEntityEvent(T entity, T oldEntity)
  {
    this.entity = entity;
    this.oldEntity = oldEntity;
    this.type = EventType.MODIFY;
  }

  //~--- methods --------------------------------------------------------------

  /**
   * Method description
   *
   *
   * @param obj
   *
   * @return
   */
  @Override
  @SuppressWarnings("unchecked")
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

    final AbstractEntityEvent<T> other = (AbstractEntityEvent<T>) obj;

    return Objects.equal(entity, other.entity)
      && Objects.equal(oldEntity, other.oldEntity)
      && Objects.equal(type, other.type);
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
    return Objects.hashCode(entity, oldEntity, type);
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
    //J-
    return MoreObjects.toStringHelper(this)
                      .add("entity", entity)
                      .add("oldEntity", oldEntity)
                      .add("type", type)
                      .toString();
    //J+
  }

  //~--- get methods ----------------------------------------------------------

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public T getEntity()
  {
    return entity;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public T getOldEntity()
  {
    return oldEntity;
  }

  /**
   * Method description
   *
   *
   * @return
   */
  @Override
  public EventType getType()
  {
    return type;
  }

  //~--- fields ---------------------------------------------------------------

  /** Field description */
  private final T entity;

  /** Field description */
  private final T oldEntity;

  /** Field description */
  private final EventType type;
}
