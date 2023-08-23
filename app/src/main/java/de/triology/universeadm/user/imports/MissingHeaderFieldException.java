package de.triology.universeadm.user.imports;

public class MissingHeaderFieldException extends Exception {

    public MissingHeaderFieldException(String errorMessage, Throwable t) {
        super(errorMessage, t);
    }
}
