package de.triology.universeadm.group;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Groups
{

  private Groups()
  {
  }
        
  public static Group createHeartOfGold(){
    return new Group(
      "Heart Of Gold", 
      "The starship Heart of Gold was the first spacecraft to make use of the Infinite Improbability Drive", 
      "dent", "trillian"
    );
  }
  
  public static Group createBrockianUltraCricket(){
    return new Group(
      "Brockian Ultra-Cricket",
      "Brockian Ultra-Cricket is a curious game which involves suddenly hitting people for no readily apparent reason and then running away.",
      "trillian"
    );
  }
}
