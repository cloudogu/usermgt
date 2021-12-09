package de.triology.universeadm;

public class DenyChangeUserResponse {
  public final String errorMessage;
  public DenyChangeUserResponse(final IllegalArgumentException e) {
    errorMessage = e.getMessage();
  }
}
