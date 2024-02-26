package de.triology.universeadm;

public class PaginationQueryOutOfRangeException extends EntityException {
    private final PaginationResult<?> result;

    public PaginationQueryOutOfRangeException(PaginationResult<?> result) {
        this.result = result;
    }

    public PaginationResult<?> getResult() {
        return result;
    }
}
