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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.unboundid.asn1.ASN1Exception;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import com.unboundid.ldap.sdk.controls.SortKey;
import com.unboundid.ldap.sdk.controls.VirtualListViewRequestControl;
import com.unboundid.ldap.sdk.controls.VirtualListViewResponseControl;
import com.unboundid.util.LDAPTestUtils;
import de.triology.universeadm.*;
import de.triology.universeadm.validation.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.triology.universeadm.AbstractLDAPManager.EQUAL;
import static de.triology.universeadm.AbstractLDAPManager.WILDCARD;
import static de.triology.universeadm.AbstractManagerResource.PAGING_MIN_PAGE;

/**
 * @param <T>
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public class MappingHandler<T extends Comparable<T>> {

    private static final Logger logger = LoggerFactory.getLogger(MappingHandler.class);

    private static final Pattern QUERY_PATTERN = Pattern.compile("[^\\(\\)\\\\]+");
    private static final Pattern HEAVY_WILDCARD = Pattern.compile("[\\*]{2,}");

    private final LDAPConnectionStrategy strategy;
    private final Mapper<T> mapper;
    private final Validator validator;
    private final String[] returningAttributes;

    public MappingHandler(LDAPConnectionStrategy strategy, Mapper<T> mapper, Validator validator) {
        this.strategy = strategy;
        this.mapper = mapper;
        this.validator = validator;
        List<String> rattrs = this.mapper.getReturningAttributes();
        this.returningAttributes = rattrs.toArray(new String[rattrs.size()]);
    }

    public void create(T object) {
        String id = mapper.getRDNValue(object);
        logger.info("create entry {}", id);
        validator.validate(object, "entity is not valid");
        Entry entry = mapper.convert(object);
        try {
            entry = consume(object, entry);
            if (entry != null) {
                if (logger.isTraceEnabled()) {
                    logger.info("create ldap entry:\n{}", LDAPUtil.toLDIF(entry));
                }
                strategy.get().add(entry);
            } else {
                logger.debug("consumer returned null entry");
            }
        } catch (LDAPException ex) {
            throw new EntityException("could not create entity ".concat(entry.getDN()), ex);
        }
    }

    public void modify(T object) {
        String id = mapper.getRDNValue(object);
        logger.info("update entity {}", id);
        try {
            String dn = searchDN(id);
            Attribute mappedObjectClasses = mapper.getObjectClasses();
            List<Modification> modifications = mapper.getModifications(object);
            modifications = consume(object, modifications);
            if (modifications != null && !modifications.isEmpty()) {
                boolean shouldValidate = false;

                for (Modification modification : modifications) {
                    shouldValidate = shouldValidate || modification.getAttributeName().equals("name");
                }

                if (shouldValidate){
                    validator.validate(object, "entity is not valid");
                }

                modifications.add(new Modification(ModificationType.REPLACE, "objectClass", mappedObjectClasses.getValues()));
                if (logger.isTraceEnabled()) {
                    logger.trace("modify ldap entry:\n{}", LDAPUtil.toLDIF(dn, modifications));
                }
                strategy.get().modify(dn, modifications);
            } else {
                logger.debug("modifications are empty after consume");
            }
        } catch (LDAPException ex) {
            if (ex.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                throw new EntityNotFoundException("could not find entity ".concat(id), ex);
            } else {
                throw new EntityException("could not modify entity ".concat(id), ex);
            }
        }
    }

    public void remove(T object) {
        String id = mapper.getRDNValue(object);
        logger.info("remove entity with id {}", id);
        try {
            String dn = searchDN(id);
            logger.trace("remove ldap entry {}", dn);
            strategy.get().delete(dn);
        } catch (LDAPException ex) {
            if (ex.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
                throw new EntityNotFoundException("could not find entity ".concat(id), ex);
            } else {
                throw new EntityException("could not remove entity ".concat(id), ex);
            }
        }
    }

    private Filter createObjectFilter(String id) {
        return Filter.createANDFilter(
            mapper.getBaseFilter(),
            Filter.createEqualityFilter(mapper.getRDNName(), id)
        );
    }

    private Entry searchForObject(String id, String... attributes) throws LDAPSearchException {
        Filter filter = createObjectFilter(id);
        return strategy.get().searchForEntry(mapper.getParentDN(), SearchScope.SUB, filter, attributes);
    }

    private String searchDN(String id) throws LDAPSearchException {
        Entry entry = searchForObject(id, mapper.getRDNName());
        if (entry == null) {
            throw new EntityNotFoundException("could not find entity with id ".concat(id));
        }
        return entry.getDN();
    }

    public T get(String id) {
        Preconditions.checkNotNull(id, "id is required");
        T entity = null;
        try {
            Entry e = searchForObject(id, returningAttributes);
            if (e != null) {
                T object = consume(e, mapper.convert(e));
                if (object != null) {
                    entity = object;
                } else {
                    logger.debug("consumer returned null for object {}", e.getDN());
                }
            }
        } catch (LDAPSearchException ex) {
            throw new EntityException("could not get entity for ".concat(id), ex);
        }

        return entity;
    }

    public List<T> queryByAttribute(String attributeName, String attributeValue) {
        Preconditions.checkNotNull(attributeName, "attributeName is required");
        Preconditions.checkNotNull(attributeValue, "attributeValue is required");
        final List<T> entities = Lists.newArrayList();

        try {
            MappingAttribute attribute = mapper.getAttribute(attributeName);
            if (attribute == null) {
                throw new EntityNotFoundException("could not find entity with name ".concat(attributeName));
            }

            // create filter for given attribute
            Filter filter = Filter.createANDFilter(mapper.getBaseFilter(), Filter.createEqualityFilter(attribute.getLdapName(), attributeValue));

            SearchRequest searchRequest = new SearchRequest(mapper.getParentDN(), SearchScope.SUB, filter, returningAttributes);
            ASN1OctetString resumeCookie = null;
            LDAPInterface connection = strategy.get();

            // query from LDAP in 100er-chunks to avoid max-results-limit of LDAP
            while (true) {
                searchRequest.setControls(new SimplePagedResultsControl(100, resumeCookie));
                SearchResult searchResult = connection.search(searchRequest);
                for (SearchResultEntry e : searchResult.getSearchEntries()) {
                    consume(entities, e);
                }

                LDAPTestUtils.assertHasControl(searchResult, SimplePagedResultsControl.PAGED_RESULTS_OID);
                SimplePagedResultsControl responseControl = SimplePagedResultsControl.get(searchResult);
                if (responseControl.moreResultsToReturn()) {
                    // The resume cookie can be included in the simple paged results
                    // control included in the next search to get the next page of results.
                    resumeCookie = responseControl.getCookie();
                } else {
                    break;
                }
            }
        } catch (LDAPException ex) {
            throw new EntityException(String.format("could not query entities by %s for %s", attributeName, attributeValue), ex);
        }

        return entities;
    }

    public PaginationResult<T> query(PaginationQuery query) {
        try {
            return doQuery(query);
        } catch (LDAPSearchException ex) {
            // retry query with page 1 to get totalEntries
            try {
                PaginationResult<T> result = doQuery(PaginationQuery.fromQueryWithNewPage(query, PAGING_MIN_PAGE));
                throw new PaginationQueryOutOfRangeException(result);
            } catch (LDAPException e) {
                throw new EntityException("could not query entities from LDAP", ex);
            }
        } catch (LDAPException ex) {
            throw new EntityException("could not query entities from LDAP", ex);
        }
    }

    private PaginationResult<T> doQuery(PaginationQuery query) throws LDAPException {
        final List<T> entities = Lists.newArrayList();

        int vlvOffset = query.getOffset() + 1;
        int vlvLimit = query.getPageSize() - 1;
        int vlvContentCount = 0;
        ASN1OctetString vlvContextID = decodeContextId(query.getContext());

        SearchRequest searchRequest = new SearchRequest(mapper.getParentDN(), SearchScope.SUB, createFilter(query.getQuery(), query.getExcludes()), returningAttributes);
        searchRequest.addControl(createSortControl(query));
        searchRequest.addControl(new VirtualListViewRequestControl(vlvOffset, 0, vlvLimit, vlvContentCount, vlvContextID));

        SearchResult result = strategy.get().search(searchRequest);
        for (SearchResultEntry e : result.getSearchEntries()) {
            consume(entities, e);
        }

        LDAPTestUtils.assertHasControl(result, VirtualListViewResponseControl.VIRTUAL_LIST_VIEW_RESPONSE_OID);
        VirtualListViewResponseControl vlvResponseControl = VirtualListViewResponseControl.get(result);
        vlvContentCount = vlvResponseControl.getContentCount();
        vlvContextID = vlvResponseControl.getContextID();

        return new PaginationResult<>(entities, vlvContentCount, encodeContextId(vlvContextID));
    }

    private ServerSideSortRequestControl createSortControl(PaginationQuery query) {
        String sortAttribute = "cn";
        MappingAttribute attribute = mapper.getAttribute(query.getSortBy());
        if (attribute != null) {
            sortAttribute = attribute.getLdapName();
        }

        return new ServerSideSortRequestControl(new SortKey(sortAttribute, "caseIgnoreOrderingMatch", query.isReverse()));
    }

    private Filter createFilter(String query, List<String> excludes) throws LDAPException {
        List<Filter> additionalFilters = Lists.newArrayList();

        if (!Strings.isNullOrEmpty(query)) {
            if (!QUERY_PATTERN.matcher(query).matches()) {
                throw new IllegalQueryException("query contain illegal characters");
            }

            String q = prepareQuery(query);
            List<Filter> queryFilters = Lists.newArrayList();
            for (String attribute : mapper.getSearchAttributes()) {
                queryFilters.add(Filter.create(attribute.concat(EQUAL).concat(q)));
            }
            additionalFilters.add(Filter.createORFilter(queryFilters));
        }


        List<Filter> excludeFilters = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(excludes)) {
            for (String exclude : excludes) {
                excludeFilters.add(Filter.create(String.format("!(%s=%s)", mapper.getRDNName(), exclude)));
            }
            additionalFilters.add(Filter.createANDFilter(excludeFilters));
        }

        Filter filter = mapper.getBaseFilter();
        if (!additionalFilters.isEmpty()) {
            additionalFilters.add(filter);
            filter = Filter.createANDFilter(additionalFilters);
        }

        logger.debug("start entity search with filter {}", filter);

        return filter;
    }

    private String prepareQuery(String query) {
        StringBuffer buffer = new StringBuffer();
        Matcher m = HEAVY_WILDCARD.matcher(query);
        while (m.find()) {
            m.appendReplacement(buffer, "*");
        }
        m.appendTail(buffer);

        String q = buffer.toString();
        if (!q.contains("*")) {
            q = WILDCARD.concat(q).concat(WILDCARD);
        }
        return q;
    }

    protected List<Modification> consume(T object, List<Modification> mods) {
        return mods;
    }

    protected Entry consume(T object, Entry entry) {
        return entry;
    }

    protected T consume(Entry entry, T object) {
        return object;
    }

    private void consume(Collection<T> collection, Entry entry) {
        T object = consume(entry, mapper.convert(entry));
        if (object != null) {
            collection.add(object);
        } else {
            logger.debug("consumer returned null for object {}", entry.getDN());
        }
    }

    private static String encodeContextId(com.unboundid.asn1.ASN1OctetString contextId) {
        if (contextId == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(contextId.encode());
    }

    private static com.unboundid.asn1.ASN1OctetString decodeContextId(String contextId) {
        if (Strings.isNullOrEmpty(contextId)) {
            return null;
        }
        try {
            return com.unboundid.asn1.ASN1OctetString.decode(Base64.getDecoder().decode(contextId)).decodeAsOctetString();
        } catch (ASN1Exception e) {
            logger.warn("error while decoding contextId.", e);
            return null;
        }
    }

}
