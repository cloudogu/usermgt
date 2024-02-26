package de.triology.universeadm;

public class PaginationErrorResponse {
    private final PaginationResultResponse.MetaData meta;
    private final PaginationResultResponse.Links links;
    private final String errorCode;
    private final String errorMsg;

    public PaginationErrorResponse(PaginationQuery query, PaginationResult<?> result, String basePath, PaginationQueryError error) {
        this.meta = new PaginationResultResponse.MetaData(query, result);
        this.links = new PaginationResultResponse.Links(query, this.meta, result.getContext(), basePath);
        this.errorCode = error.name();
        this.errorMsg = error.name();
    }

    public PaginationResultResponse.MetaData getMeta() {
        return meta;
    }

    public PaginationResultResponse.Links getLinks() {
        return links;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}


