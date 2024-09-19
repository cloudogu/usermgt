package de.triology.universeadm;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Unsupported
{
  
  private Unsupported(){}
  
  public static UnsupportedOperationException unsupportedOperation(){
    return new UnsupportedOperationException("Not supported yet.");
  }
  
}
