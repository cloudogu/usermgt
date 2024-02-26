package de.triology.universeadm;

import com.google.common.base.Strings;

import javax.ws.rs.core.UriBuilder;
import java.util.List;

import static de.triology.universeadm.AbstractManagerResource.PAGING_MIN_PAGE;

public class PaginationResultResponse<T> {
    private final List<T> data;
    private final MetaData meta;
    private final Links links;

    public PaginationResultResponse(PaginationQuery query, PaginationResult<T> result, String basePath) {
        this.data = result.getEntries();
        this.meta = new MetaData(query, result);
        this.links = new Links(query, this.meta, result.getContext(), basePath);
    }

    public List<T> getData() {
        return data;
    }

    public MetaData getMeta() {
        return meta;
    }

    public Links getLinks() {
        return links;
    }

    public static class MetaData {
        private final int page;
        private final int pageSize;
        private final int totalPages;
        private final int totalItems;
        private final int startItem;
        private final int endItem;
        private final String context;

        public MetaData(PaginationQuery query, PaginationResult<?> result) {
            this.page = query.getPage();
            this.pageSize = query.getPageSize();
            this.totalItems = result.getTotalEntries();
            this.totalPages = calculateTotalPages(result.getTotalEntries(), query.getPageSize());
            this.startItem = (page - 1) * pageSize + 1;
            this.endItem = Math.min(this.totalItems, page * pageSize);
            this.context = result.getContext();
        }

        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pageSize;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getStartItem() {
            return startItem;
        }

        public int getEndItem() {
            return endItem;
        }

        public String getContext() {
            return context;
        }
    }

    private static int calculateTotalPages(int totalEntries, int pageSize) {
        return (int) Math.ceil((double) totalEntries / pageSize);
    }

    public static class Links {
        private final String self;
        private final String first;
        private final String prev;
        private final String next;
        private final String last;

        public Links(PaginationQuery query, MetaData metaData, String context, String basePath) {
            int currentPage = query.getPage();
            int lastPage = metaData.getTotalPages();
            int previousPage = (currentPage <= PAGING_MIN_PAGE || currentPage > lastPage) ? -1 : (currentPage - 1);
            int nextPage = (currentPage >= lastPage || currentPage < PAGING_MIN_PAGE) ? -1 : (currentPage + 1);

            UriBuilder builder = UriBuilder.fromUri(basePath)
                .replaceQuery(query.createUriQuery());

            if (!Strings.isNullOrEmpty(context)) {
                builder.replaceQueryParam("context", context);
            }

            this.self = builder.replaceQueryParam("page", currentPage).build().toString();
            this.first = builder.replaceQueryParam("page", PAGING_MIN_PAGE).build().toString();
            this.prev = previousPage < PAGING_MIN_PAGE ? null : builder.replaceQueryParam("page", previousPage).build().toString();
            this.next = nextPage < PAGING_MIN_PAGE ? null : builder.replaceQueryParam("page", nextPage).build().toString();
            this.last = builder.replaceQueryParam("page", lastPage).build().toString();
        }

        public String getSelf() {
            return self;
        }

        public String getFirst() {
            return first;
        }

        public String getPrev() {
            return prev;
        }

        public String getNext() {
            return next;
        }

        public String getLast() {
            return last;
        }

    }
}


