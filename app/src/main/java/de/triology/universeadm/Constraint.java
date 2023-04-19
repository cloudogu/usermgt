package de.triology.universeadm;

/**
 * Basic class to define a Constraint such as a unique attribute.
 * Used to validate LDAP entries.
 * @param <T> Specifies for which kind of ldap entry this constraint is for.
 */
public abstract class Constraint<T> {
  public abstract boolean violatedBy(T obj, Category category);
  public abstract ID getUniqueID();

  /**
   * Defines the actual ID of a constraint.
   */
  public enum ID {
    UNIQUE_USERNAME,
    UNIQUE_EMAIL,
    UNIQUE_GROUP_NAME
  }

  /**
   * Defines the situation in which a constraint can be applied.
   * For example at creation of an entry or when modifiying an entry.
   */
  public enum Category{
    CREATE,
    MODIFY
  }
}
