package de.triology.universeadm;

public abstract class Constraint<T> {
  public abstract boolean violatedBy(T obj);
  public abstract Type getType();

  public enum Type {
    UNIQUE_USERNAME,
    UNIQUE_EMAIL
  }
}
