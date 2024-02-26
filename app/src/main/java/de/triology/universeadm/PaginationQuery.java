package de.triology.universeadm;

import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;

import javax.ws.rs.core.UriBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.triology.universeadm.AbstractManagerResource.*;

public class PaginationQuery {

  private final int page;
  private final int pageSize;
  private final String query;
  private final String context;
  private final String sortBy;
  private final boolean reverse;
  private final List<String> excludes;

  public PaginationQuery(int page, int pageSize) {
    this(page, pageSize, null, null, null, null, false);
  }

  public PaginationQuery(int page, int pageSize, String query, String context, String exclude, String sortBy, boolean reverse) {
    if (page <= 0) {
      page = PAGING_DEFAULT_PAGE;
    }

    if (pageSize <= 0 || pageSize > PAGING_MAXIMUM_PAGE_SIZE) {
      pageSize = PAGING_DEFAULT_PAGE_SIZE;
    }

    this.page = page;
    this.pageSize = pageSize;
    this.query = query;

    if (!Strings.isNullOrEmpty(exclude)) {
      this.excludes = Arrays.asList(exclude.split(","));
    } else {
      this.excludes = Collections.emptyList();
    }

    this.sortBy = sortBy;
    this.reverse = reverse;

    this.context = context;
  }

  public String createUriQuery() {
    UriBuilder builder = UriBuilder.fromUri("")
      .queryParam("page", page)
      .queryParam("page_size", pageSize);

    if (!Strings.isNullOrEmpty(query)) {
      builder.queryParam("query", query);
    }

    if (!Strings.isNullOrEmpty(sortBy)) {
      builder.queryParam("sort_by", sortBy);
    }

    if (reverse) {
      builder.queryParam("reverse", true);
    }

    if (!Strings.isNullOrEmpty(context)) {
      builder.queryParam("context", context);
    }

    if (CollectionUtils.isNotEmpty(excludes)) {
      builder.queryParam("exclude", String.join(",", excludes));
    }

    return builder.build().getQuery();
  }

  public int getPage() {
    return page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public int getOffset() {
    return (page - 1) * pageSize;
  }

  public String getQuery() {
    return query;
  }

  public String getContext() {
    return context;
  }

  public List<String> getExcludes() {
    return excludes;
  }

  public String getSortBy() {
    return sortBy;
  }

  public boolean isReverse() {
    return reverse;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PaginationQuery that = (PaginationQuery) o;
    return page == that.page && pageSize == that.pageSize && reverse == that.reverse && Objects.equals(query, that.query) && Objects.equals(context, that.context) && Objects.equals(sortBy, that.sortBy) && Objects.equals(excludes, that.excludes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(page, pageSize, query, context, sortBy, reverse, excludes);
  }
}

