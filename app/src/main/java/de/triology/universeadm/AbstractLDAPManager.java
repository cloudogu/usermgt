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

    private static final Logger logger = LoggerFactory.getLogger(AbstractLDAPManager.class);

    protected List<Constraint<T>> constraints;

    public AbstractLDAPManager() {
        this.constraints = new ArrayList<>();
    }

    @Override
    public void modify(T object) {
        modify(object, true);
    }

    @Override
    public PagedResultList<T> getAll(int start, int limit) {
        logger.debug("get paged entities, start={} and limit={}", start, limit);
        return Paginations.createPaging(getAll(), start, limit);
    }

    protected abstract String typeToString(final T e);

    @Override
    public PagedResultList<T> search(String query, int start, int limit) {
        logger.debug("search paged entities, query={}, start={} and limit={}", query, start, limit);
        return Paginations.createPaging(search(query), start, limit);
    }

    public PagedResultList<T> search(final String query, final int start, final int limit, final List<String> exclude) {
        logger.debug("search paged entities, query={}, start={} and limit={} and exclude={}", query, start, limit, String.join(",", exclude));
        final List<T> searchResults = search(query)
                .stream()
                .filter(t -> !exclude.contains(this.typeToString(t)))
                .collect(Collectors.toList());
        return Paginations.createPaging(searchResults, start, limit);
    }

}
