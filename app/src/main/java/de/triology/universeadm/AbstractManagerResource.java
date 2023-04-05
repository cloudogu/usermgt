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

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * @param <T>
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public abstract class AbstractManagerResource<T> {

    public static final int PAGING_DEFAULT_START = 0;

    public static final int PAGING_DEFAULT_LIMIT = 20;

    public static final int PAGING_MAXIMUM = 100000;

    /**
     * the logger for UserResource
     */
    private static final Logger logger
            = LoggerFactory.getLogger(AbstractManagerResource.class);

    //~--- constructors ---------------------------------------------------------

    /**
     * Constructs ...
     *
     * @param manager
     */
    public AbstractManagerResource(Manager<T> manager) {
        this.manager = manager;
    }

    //~--- methods --------------------------------------------------------------

    /**
     * Method description
     *
     * @param uriInfo
     * @param object)
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Context UriInfo uriInfo, T object) {
        Response.ResponseBuilder builder;

        String id = getId(object);
        try {
            manager.create(object);

            UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());

            uriBuilder.path(id);
            builder = Response.created(uriBuilder.build());
        } catch (ConstraintViolationException e) {
            logger.warn("entity {} violates constraints", id);
            builder = Response.status(Response.Status.CONFLICT).entity(new ConstraintViolationResponse(e));
        }

        return builder.build();
    }

    protected abstract String getId(T object);

    /**
     * Method description
     *
     * @param id
     * @param object
     * @return
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response modify(@PathParam("id") String id, T object) {
        Response.ResponseBuilder builder;
        try {
            prepareForModify(id, object);
            manager.modify(object);
            builder = Response.noContent();
        } catch (ConstraintViolationException e) {
            builder = Response.status(Response.Status.CONFLICT).entity(new ConstraintViolationResponse(e));
        } catch (EntityNotFoundException ex) {
            builder = Response.status(Response.Status.NOT_FOUND);
        }

        return builder.build();
    }

    protected abstract void prepareForModify(String id, T object);

    /**
     * Method description
     *
     * @param id
     * @return
     */
    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") String id) {
        Response.ResponseBuilder builder;
        T object = manager.get(id);

        if (object == null) {
            builder = Response.status(Response.Status.NOT_FOUND);
            return builder.build();
        }
        try {
            manager.remove(object);
            builder = Response.noContent();
        } catch (CannotRemoveException e) {
            builder = Response.status(Response.Status.CONFLICT);
        }

        return builder.build();


    }

    //~--- get methods ----------------------------------------------------------

    /**
     * Method description
     *
     * @param id
     * @return
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {
        T object = manager.get(id);
        Response.ResponseBuilder builder;
        if (object != null) {
            builder = Response.ok(object);
        } else {
            builder = Response.status(Response.Status.NOT_FOUND);
        }
        return builder.build();
    }

    /**
     * Method description
     *
     * @param s
     * @param l
     * @param query
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("start") int s, @QueryParam("limit") int l, @QueryParam("query") String query) {
        int start = s;
        int limit = l;
        if (start < 0) {
            start = PAGING_DEFAULT_START;
        }

        if (limit <= 0 || limit > PAGING_MAXIMUM) {
            limit = PAGING_DEFAULT_LIMIT;
        }

        PagedResultList<T> result;
        if (Strings.isNullOrEmpty(query)) {
            result = manager.getAll(start, limit);
        } else {
            result = manager.search(query, start, limit);
        }

        Response.ResponseBuilder builder;
        if (result != null) {
            builder = Response.ok(result);
        } else if (Strings.isNullOrEmpty(query)) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } else {
            builder = Response.noContent();
        }
        return builder.build();
    }

    //~--- fields ---------------------------------------------------------------
    /**
     * Field description
     */
    private final Manager<T> manager;
}
