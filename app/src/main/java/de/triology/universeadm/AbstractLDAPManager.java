package de.triology.universeadm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <T>
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public abstract class AbstractLDAPManager<T> implements Manager<T> {

    public static final String WILDCARD = "*";

    public static final String EQUAL = "=";

    protected List<Constraint<T>> constraints;

    public AbstractLDAPManager() {
        this.constraints = new ArrayList<>();
    }

    @Override
    public void modify(T object) {
        modify(object, true);
    }

    protected abstract String typeToString(final T e);
}
