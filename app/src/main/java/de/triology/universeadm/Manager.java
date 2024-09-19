package de.triology.universeadm;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
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
   * @param fireEvent
   */
  public void modify(T object, boolean fireEvent);

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
   * @param query
   *
   * @return
   */
  public PaginationResult<T> query(PaginationQuery query);
}
