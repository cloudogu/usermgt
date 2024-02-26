package de.triology.universeadm;

import com.google.common.base.Strings;

import javax.ws.rs.core.UriBuilder;
import java.util.List;

import static de.triology.universeadm.AbstractManagerResource.PAGING_DEFAULT_PAGE;

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

  public class MetaData {
    private final int page;
    private final int pageSize;
    private final int totalPages;
    private final int totalItems;

    public MetaData(PaginationQuery query, PaginationResult<T> result) {
      this.page = query.getPage();
      this.pageSize = query.getPageSize();
      this.totalItems = result.getTotalEntries();
      this.totalPages = calculateTotalPages(result.getTotalEntries(), query.getPageSize());
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
  }

  private static int calculateTotalPages(int totalEntries, int pageSize) {
    return (int) Math.ceil((double) totalEntries / pageSize);
  }

  public class Links {
    private final String self;
    private final String first;
    private final String prev;
    private final String next;
    private final String last;

    public Links(PaginationQuery query, MetaData metaData, String context, String basePath) {
      int currentPage = query.getPage();
      int lastPage = metaData.getTotalPages();
      int previousPage = currentPage <= PAGING_DEFAULT_PAGE ? PAGING_DEFAULT_PAGE : (currentPage - 1);
      int nextPage = currentPage >= lastPage ? lastPage : (currentPage + 1);

      UriBuilder builder = UriBuilder.fromUri(basePath)
        .replaceQuery(query.createUriQuery());

      if (!Strings.isNullOrEmpty(context)) {
        builder.replaceQueryParam("context", context);
      }

      this.self  = builder.replaceQueryParam("page", currentPage).build().toString();
      this.first = builder.replaceQueryParam("page", PAGING_DEFAULT_PAGE).build().toString();
      this.prev  = builder.replaceQueryParam("page", previousPage).build().toString();
      this.next  = builder.replaceQueryParam("page", nextPage).build().toString();
      this.last  = builder.replaceQueryParam("page", lastPage).build().toString();
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


