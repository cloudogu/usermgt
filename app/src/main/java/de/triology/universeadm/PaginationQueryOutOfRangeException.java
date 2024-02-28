package de.triology.universeadm;

public class PaginationQueryOutOfRangeException extends EntityException {
    private final transient PaginationResult<?> result;

    public PaginationQueryOutOfRangeException(PaginationResult<?> result) {
        this.result = result;
    }

    @java.lang.SuppressWarnings("java:S1452")
    public PaginationResult<?> getResult() {
        return result;
    }
}
