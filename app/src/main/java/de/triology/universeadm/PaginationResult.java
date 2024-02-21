package de.triology.universeadm;

import java.util.List;

public class PaginationResult<T> {

  private final List<T> entries;
  private final int totalEntries;
  private final String context;

  public PaginationResult(List<T> entries, int totalEntries, String context) {
    this.entries = entries;
    this.totalEntries = totalEntries;
    this.context = context;
  }

  public List<T> getEntries() {
    return entries;
  }

  public int getTotalEntries() {
    return totalEntries;
  }

  public String getContext() {
    return context;
  }
}
