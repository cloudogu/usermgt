package de.triology.universeadm;

import junit.framework.TestCase;

import java.util.Arrays;

public class PaginationQueryTest extends TestCase {

    public void testNewQuery() {
        PaginationQuery query = new PaginationQuery(-1, 0, "test", "ctx", "a,b,c", "name", true);

        assertEquals(1, query.getPage());
        assertEquals(20, query.getPageSize());
    }

    public void testFromQueryWithNewPage() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        PaginationQuery newQuery = PaginationQuery.fromQueryWithNewPage(query, 1);

        String uri = newQuery.createUriQuery();
        assertEquals("page=1&page_size=20&query=test&sort_by=name&reverse=true&context=ctx&exclude=a,b,c", uri);
    }

    public void testCreateUriQuery() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);

        String uri = query.createUriQuery();
        assertEquals("page=3&page_size=20&query=test&sort_by=name&reverse=true&context=ctx&exclude=a,b,c", uri);
    }

    public void testGetPage() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals(3, query.getPage());
    }

    public void testGetPageSize() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals(20, query.getPageSize());
    }

    public void testGetOffset() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals(40, query.getOffset());
    }

    public void testGetQuery() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals("test", query.getQuery());
    }

    public void testGetContext() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals("ctx", query.getContext());
    }

    public void testGetExcludes() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals(Arrays.asList("a", "b", "c"), query.getExcludes());
    }

    public void testGetSortBy() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertEquals("name", query.getSortBy());
    }

    public void testIsReverse() {
        PaginationQuery query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", true);
        assertTrue(query.isReverse());

        query = new PaginationQuery(3, 20, "test", "ctx", "a,b,c", "name", false);
        assertFalse(query.isReverse());
    }
}
