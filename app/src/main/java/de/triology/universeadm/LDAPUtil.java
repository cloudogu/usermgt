package de.triology.universeadm;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldif.LDIFAddChangeRecord;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import java.util.List;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class LDAPUtil
{
  private static final int WRAP_COLUMN = 999;
  
  private LDAPUtil(){}
  
  public static String toLDIF(String dn, List<Modification> modifications){
    LDIFModifyChangeRecord ldif = new LDIFModifyChangeRecord(dn, modifications);
    return ldif.toLDIFString(WRAP_COLUMN);
  }
  
  public static String toLDIF(Entry entry){
    LDIFAddChangeRecord ldif = new LDIFAddChangeRecord(entry);
    return ldif.toLDIFString(WRAP_COLUMN);
  }
}
