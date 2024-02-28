package de.triology.universeadm;

import junit.framework.TestCase;

import java.util.Collections;

public class PaginationErrorResponseTest extends TestCase {

    public void testGetMeta() {
        PaginationQuery query = new PaginationQuery(34, 20);
        PaginationResult<?> result = new PaginationResult<>(Collections.emptyList(), 543, "ctx");
        PaginationErrorResponse errorResponse = new PaginationErrorResponse(query, result, "/base/Path", PaginationQueryError.ERR_OUT_OF_RANGE);

        PaginationResultResponse.MetaData meta = errorResponse.getMeta();
        assertNotNull(meta);
        assertEquals("ctx", meta.getContext());
        assertEquals(34, meta.getPage());
        assertEquals(20, meta.getPageSize());
        assertEquals(28, meta.getTotalPages());
        assertEquals(543, meta.getTotalItems());
        assertEquals(661, meta.getStartItem());
        assertEquals(543, meta.getEndItem());
    }

    public void testGetLinks() {
        PaginationQuery query = new PaginationQuery(34, 20);
        PaginationResult<?> result = new PaginationResult<>(Collections.emptyList(), 543, "ctx");
        PaginationErrorResponse errorResponse = new PaginationErrorResponse(query, result, "/base/Path", PaginationQueryError.ERR_OUT_OF_RANGE);

        PaginationResultResponse.Links links = errorResponse.getLinks();
        assertNotNull(links);
        assertEquals("/base/Path?page_size=20&context=ctx&page=34", links.getSelf());
        assertEquals("/base/Path?page_size=20&context=ctx&page=1", links.getFirst());
        assertEquals("/base/Path?page_size=20&context=ctx&page=28", links.getLast());
        assertNull(links.getPrev());
        assertNull(links.getNext());
    }

    public void testGetErrorCode() {
        PaginationQuery query = new PaginationQuery(34, 20);
        PaginationResult<?> result = new PaginationResult<>(Collections.emptyList(), 543, "ctx");
        PaginationErrorResponse errorResponse = new PaginationErrorResponse(query, result, "/base/Path", PaginationQueryError.ERR_OUT_OF_RANGE);

        assertEquals(PaginationQueryError.ERR_OUT_OF_RANGE.name(), errorResponse.getErrorCode());
    }

    public void testGetErrorMsg() {
        PaginationQuery query = new PaginationQuery(34, 20);
        PaginationResult<?> result = new PaginationResult<>(Collections.emptyList(), 543, "ctx");
        PaginationErrorResponse errorResponse = new PaginationErrorResponse(query, result, "/base/Path", PaginationQueryError.ERR_OUT_OF_RANGE);

        assertEquals(PaginationQueryError.ERR_OUT_OF_RANGE.name(), errorResponse.getErrorMsg());
    }
}
