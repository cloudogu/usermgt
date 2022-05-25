package de.triology.universeadm.mapping;

/**
 * Converter for the conversion of a Java Boolean to an LDAP Boolean and vice versa.
 *
 * In LDAP, the boolean values are capitalised ("FALSE", "TRUE).
 */
public class LDAPBooleanConverter extends AbstractMappingConverter {

    private static final String LDAP_FALSE = "FALSE";
    private static final String LDAP_TRUE = "TRUE";

    @Override
    public String encodeAsString(Object object) {
        if (object instanceof Boolean) {
            Boolean bool = (Boolean) object;

            if (Boolean.TRUE.equals(bool)) {
                return LDAP_TRUE;
            } else {
                return LDAP_FALSE;
            }
        }

        return LDAP_FALSE;
    }

    @Override
    public <T> Object decodeFromString(FieldDescriptor<T> type, String string) {
        return LDAP_TRUE.equals(string);
    }
}
