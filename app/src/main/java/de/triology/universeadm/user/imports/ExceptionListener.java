package de.triology.universeadm.user.imports;

/**
 * ExceptionListener can be used to inform entities about exceptions without throwing them. This con be useful in
 * the context of lambda expression, where checked exceptions are difficult to handle.
 * @param <T> subclass of {@link Exception}
 */
@FunctionalInterface
public interface ExceptionListener<T extends Exception> {
    /**
     * Notifies the listener of an exception that would otherwise have been thrown.
     * @param e Exception that occurred.
     */
    void notify(T e);
}
