package de.triology.universeadm;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static de.triology.universeadm.AbstractManagerResource.*;

public class PaginationQuery {

  private final int offset;
  private final int limit;
  private final String query;
  private final String contextId;
  private final List<String> excludes;

  public PaginationQuery(int offset, int limit, String query, String exclude, String contextId) {
    if (offset < 0) {
      offset = PAGING_DEFAULT_START;
    }

    if (limit <= 0 || limit > PAGING_MAXIMUM) {
      limit = PAGING_DEFAULT_LIMIT;
    }

    this.offset = offset;
    this.limit = limit;
    this.query = query;

    if (!Strings.isNullOrEmpty(exclude)) {
      this.excludes = Arrays.asList(exclude.split(","));
    } else {
      this.excludes = Collections.emptyList();
    }

    this.contextId = contextId;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  public String getQuery() {
    return query;
  }

  public String getContextId() {
    return contextId;
  }

  public List<String> getExcludes() {
    return excludes;
  }
}
