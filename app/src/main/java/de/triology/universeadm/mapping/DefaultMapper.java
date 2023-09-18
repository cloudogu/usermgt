/*
 * Copyright (c) 2013 - 2014, TRIOLOGY GmbH
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://www.scm-manager.com
 */

package de.triology.universeadm.mapping;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * @param <T>
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class DefaultMapper<T> implements Mapper<T> {

    private final MappingConverterFactory converterFactory;
    private final Mapping mapping;
    private final String parentDN;
    private final ClassDescriptor<T> type;
    private final List<String> returningAttributes;
    private final Attribute objectClasses;
    private final MappingAttribute rdn;
    private final Filter baseFilter;
    private final List<String> searchAttributes;

    public DefaultMapper(MappingConverterFactory converterFactory, Mapping mapping, Class<T> type, String parentDN) {
        Preconditions.checkNotNull(converterFactory, "converterFactory is required");
        Preconditions.checkNotNull(mapping, "mapping is required");
        Preconditions.checkNotNull(parentDN, "parentDN is required");
        this.converterFactory = converterFactory;
        this.mapping = mapping;
        this.parentDN = parentDN;
        this.type = new ClassDescriptor<>(type);
        this.returningAttributes = Lists.transform(mapping.getAttributes(), toLdapName);
        this.searchAttributes = FluentIterable.from(mapping.getAttributes()).filter(searchPredicate).transform(toLdapName).toList();
        this.objectClasses = new Attribute("objectClass", mapping.getObjectClasses());
        this.rdn = extractRdn();

        Filter rdnPresence = Filter.createPresenceFilter(rdn.getLdapName());
        String mfilter = mapping.getBaseFilter();
        if (!Strings.isNullOrEmpty(mfilter)) {
            try {
                this.baseFilter = Filter.createANDFilter(Filter.create(mfilter), rdnPresence);
            } catch (LDAPException ex) {
                throw new MappingException("could not create base filter from ".concat(mfilter), ex);
            }
        } else {
            this.baseFilter = rdnPresence;
        }
    }

    @Override
    public String getRDNName() {
        return rdn.getLdapName();
    }

    private MappingAttribute extractRdn() {
        Iterable<MappingAttribute> rdns = Iterables.filter(mapping.getAttributes(), rdnPredicate);
        int size = Iterables.size(rdns);
        if (size <= 0) {
            throw new MappingException("no attribute marked as rdn");
        } else if (size > 1) {
            throw new MappingException("more than one attribute is marked as rdn");
        }
        return Iterables.getOnlyElement(rdns);
    }

    @Override
    public String getRDNValue(T object) {
        Object value = getObjectValue(object, rdn);
        if (value == null) {
            throw new MappingException("object does not contain a rdn");
        }
        return value.toString();
    }

    @Override
    public String getParentDN() {
        return parentDN;
    }

    @Override
    public Filter getBaseFilter() {
        return baseFilter;
    }

    @Override
    public List<String> getSearchAttributes() {
        return searchAttributes;
    }

    private String getDN(String rdn) {
        StringBuilder dn = new StringBuilder(this.rdn.getLdapName());
        return dn.append("=").append(rdn).append(",").append(parentDN).toString();
    }

    @Override
    public List<String> getReturningAttributes() {
        return returningAttributes;
    }

    @Override
    public Entry convert(T object) {
        List<Attribute> attributes = Lists.newArrayList();
        attributes.add(objectClasses);

        String value = null;

        for (MappingAttribute ma : filter(mapping.getAttributes(), createPredicate)) {
            Attribute attribute = createAttribute(ma, object);
            if (attribute != null) {
                if (ma.isRdn()) {
                    value = attribute.getValue();
                }

                attributes.add(attribute);
                List<String> siblings = ma.getSiblings();
                for (String sibling : siblings) {
                    attributes.add(new Attribute(sibling, attribute.getRawValues()));
                }
            }
        }

        if (Strings.isNullOrEmpty(value)) {
            throw new MappingException("entry does not have an rdn");
        }
        return new Entry(getDN(value), attributes);
    }

    private Attribute createAttribute(MappingAttribute ma, T object) {
        Attribute attribute = null;
        Object value = getObjectValue(object, ma);
        if (value != null) {
            attribute = createAttributeWithValue(ma, value);
        }

        return attribute;
    }

    private Object getObjectValue(Object object, MappingAttribute ma) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty(object, ma.getName());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new MappingException("could not convert object to entry", ex);
        }
        return value;
    }

    public Attribute getObjectClasses() {
        return objectClasses;
    }

    @Override
    public T convert(Entry entry) {
        T object = type.newInstance();
        for (MappingAttribute ma : filter(mapping.getAttributes(), readPredicate)) {
            FieldDescriptor<T> desc = type.getField(ma.getName());
            if (desc == null) {
                throw new MappingException("could not find descriptor for field ".concat(ma.getName()));
            }
            Attribute attribute = entry.getAttribute(ma.getLdapName());
            if (attribute != null) {
                Object value = getObjectValue(ma, desc, attribute);
                setObjectValue(object, ma, value);
            }
        }
        return object;
    }

    private void setObjectValue(Object object, MappingAttribute ma, Object value) {
        try {
            PropertyUtils.setProperty(object, ma.getName(), value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new MappingException("could not set object value", ex);
        }
    }

    private Iterable<MappingAttribute> filter(Iterable<MappingAttribute> iterable, Predicate<MappingAttribute> predicate) {
        return Iterables.filter(iterable, predicate);
    }

    @Override
    public List<Modification> getModifications(T object) {
        List<Modification> modifications = Lists.newArrayList();
        for (MappingAttribute ma : filter(mapping.getAttributes(), modifyPredicate)) {
            Object value = getObjectValue(object, ma);
            Modification modification = createModification(ma, value);
            modifications.add(modification);
            List<String> siblings = ma.getSiblings();
            for (String sibling : siblings) {
                ASN1OctetString[] data = modification.getRawValues();
                if (data != null) {
                    modifications.add(new Modification(modification.getModificationType(), sibling, data));
                } else {
                    modifications.add(new Modification(modification.getModificationType(), sibling));
                }
            }
        }
        return modifications;
    }

    private Object getObjectValue(MappingAttribute ma, FieldDescriptor<T> desc, Attribute attribute) {
        Object value;
        if (ma.isBinary()) {
            if (ma.isMultiValue()) {
                value = converterFactory.getDecoder(ma).decodeFromMultiBytes(desc, attribute.getValueByteArrays());
            } else {
                value = converterFactory.getDecoder(ma).decodeFromBytes(desc, attribute.getValueByteArray());
            }
        } else {
            if (ma.isMultiValue()) {
                value = converterFactory.getDecoder(ma).decodeFromMultiString(desc, attribute.getValues());
            } else {
                value = converterFactory.getDecoder(ma).decodeFromString(desc, attribute.getValue());
            }
        }
        return value;
    }

    private Modification createModification(MappingAttribute ma, Object value) {
        String name = ma.getLdapName();

        if (ma.isBinary()) {
            if (ma.isMultiValue()) {
                byte[][] bytes = converterFactory.getEncoder(ma).encodeAsMultiBytes(value);
                return new Modification(ModificationType.REPLACE, name, bytes);
            } else {
                return new Modification(ModificationType.REPLACE, name, converterFactory.getEncoder(ma).encodeAsBytes(value));
            }
        } else {
            if (ma.isMultiValue()) {
                String[] strings = converterFactory.getEncoder(ma).encodeAsMultiString(value);
                return new Modification(ModificationType.REPLACE, name, strings);
            } else {
                String modValue = converterFactory.getEncoder(ma).encodeAsString(value);
                if (Strings.isNullOrEmpty(modValue)) {
                    return new Modification(ModificationType.REPLACE, name);
                } else {
                    return new Modification(ModificationType.REPLACE, name, modValue);
                }
            }
        }
    }

    private Attribute createAttributeWithValue(MappingAttribute ma, Object value) {
        Attribute attribute = null;
        String name = ma.getLdapName();

        if (ma.isBinary()) {
            if (ma.isMultiValue()) {
                byte[][] bytes = converterFactory.getEncoder(ma).encodeAsMultiBytes(value);
                attribute = new Attribute(name, bytes);
            } else {
                attribute = new Attribute(name, converterFactory.getEncoder(ma).encodeAsBytes(value));
            }
        } else {
            if (ma.isMultiValue()) {
                String[] strings = converterFactory.getEncoder(ma).encodeAsMultiString(value);
                if (strings != null && strings.length > 0) {
                    attribute = new Attribute(name, strings);
                }
            } else {
                String string = converterFactory.getEncoder(ma).encodeAsString(value);
                if (!Strings.isNullOrEmpty(string)) {
                    attribute = new Attribute(name, string);
                }
            }
        }
        return attribute;
    }


    // private classes
    private static final Function<MappingAttribute, String> toLdapName = new Function<MappingAttribute, String>() {

        @Override
        public String apply(MappingAttribute input) {
            return input.getLdapName();
        }
    };

    private static final Predicate<MappingAttribute> rdnPredicate = new Predicate<MappingAttribute>() {

        @Override
        public boolean apply(MappingAttribute input) {
            return input.isRdn();
        }
    };

    private static final Predicate<MappingAttribute> createPredicate = new Predicate<MappingAttribute>() {

        @Override
        public boolean apply(MappingAttribute input) {
            return input.isInCreate();
        }
    };

    private static final Predicate<MappingAttribute> modifyPredicate = new Predicate<MappingAttribute>() {

        @Override
        public boolean apply(MappingAttribute input) {
            return input.isInModify();
        }
    };

    private static final Predicate<MappingAttribute> readPredicate = new Predicate<MappingAttribute>() {

        @Override
        public boolean apply(MappingAttribute input) {
            return input.isInRead();
        }
    };

    private static final Predicate<MappingAttribute> searchPredicate = new Predicate<MappingAttribute>() {

        @Override
        public boolean apply(MappingAttribute input) {
            return input.isInSearch();
        }
    };

}
