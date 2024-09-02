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
import de.triology.universeadm.user.UserResource;
import de.triology.universeadm.user.imports.FieldConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * @param <T>
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public abstract class AbstractManagerResource<T> {

    public static final int PAGING_MIN_PAGE = 1;

    public static final int PAGING_DEFAULT_PAGE_SIZE = 20;

    public static final int PAGING_MAXIMUM_PAGE_SIZE = 100000;

    private static final Logger logger
        = LoggerFactory.getLogger(AbstractManagerResource.class);

    private static final String constraintViolationLogMsg = "entity {} violates constraints";

    @Context
    protected UriInfo uriInfo;

    /**
     * Constructs a new AbstractManagerResource. Must be called from a concrete implementation's constructor.
     *
     * @param manager -
     */
    public AbstractManagerResource(Manager<T> manager) {
        this.manager = manager;
    }

    protected String getCurrentPath() {
        return UriBuilder.fromUri(this.uriInfo.getBaseUri().getPath()).path(this.uriInfo.getPath()).build().toString();
    }

    /**
     * Modify takes an abstract entity id and creates the corresponding entity in the database.
     *
     * <p>
     *     The success relies on the underlying manager implementation (for users/groups/etc). A HTTP 409 CONFLICT response will be rendered if either a field is marked as unique over all database records and the update implies a duplicated field, or if other constraints are violated. This includes if the record being updated was created in the meantime.
     * </p>
     *
     * @param uriInfo -
     * @param object - the object that represents the updated entity
     * @return a webservice response according the success of the update.
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
        } catch (UniqueConstraintViolationException e) {
            logger.warn(constraintViolationLogMsg, id);
            builder = Response.status(Response.Status.CONFLICT).entity(new UniqueConstraintViolationResponse(e));
        } catch (FieldConstraintViolationException e) {
            logger.warn(constraintViolationLogMsg, id);
            builder = Response.status(Response.Status.CONFLICT).entity(new FieldConstraintViolationResponse(e));
        }

        return builder.build();
    }

    protected abstract String getId(T object);

    protected abstract String getDefaultSortAttribute();

    /**
     * Modify takes an abstract entity id and updates the corresponding entity in the database.
     *
     * <p>
     *     The success relies on the underlying manager implementation (for users/groups/etc). A HTTP 409 CONFLICT response will be rendered if either a field is marked as unique over all database records and the update implies a duplicated field, or if other constraints are violated. If the record being updated was deleted in the meantime, a HTTP 404 NOT FOUND will be rendered.
     * </p>
     *
     * @param id - the entity's unique identifier
     * @param object - the object that represents the updated entity
     * @return a webservice response according the success of the update.
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
        } catch (UniqueConstraintViolationException e) {
            logger.warn(constraintViolationLogMsg, id);
            builder = Response.status(Response.Status.CONFLICT).entity(new UniqueConstraintViolationResponse(e));
        } catch (EntityNotFoundException ex) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } catch (FieldConstraintViolationException e) {
            logger.warn(constraintViolationLogMsg, id);
            builder = Response.status(Response.Status.CONFLICT).entity(new FieldConstraintViolationResponse(e));
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
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(
        @QueryParam("page") int page,
        @QueryParam("page_size") int pageSize,
        @QueryParam("query") String query,
        @QueryParam("context") String context,
        @QueryParam("exclude") final String exclude,
        @QueryParam("sort_by") final String sortBy,
        @QueryParam("reverse") final boolean reverse
    ) {
        String sortAttribute = sortBy;
        if (Strings.isNullOrEmpty(sortBy)) {
            sortAttribute = getDefaultSortAttribute();
        }

        PaginationQuery paginationQuery = new PaginationQuery(page, pageSize, query, context, exclude, sortAttribute, reverse);

        Response.ResponseBuilder builder;
        try {
            PaginationResult<T> result = manager.query(paginationQuery);

            if (result != null) {
                PaginationResultResponse<T> resultResponse = new PaginationResultResponse<>(paginationQuery, result, getCurrentPath());
                builder = Response.ok(resultResponse);
            } else if (Strings.isNullOrEmpty(query)) {
                builder = Response.status(Response.Status.NOT_FOUND);
            } else {
                builder = Response.noContent();
            }
        } catch (PaginationQueryOutOfRangeException ex) {
            builder = Response.status(Response.Status.BAD_REQUEST).entity(new PaginationErrorResponse(paginationQuery, ex.getResult(), getCurrentPath(), PaginationQueryError.ERR_OUT_OF_RANGE));
        }

        return builder.build();
    }

    //~--- fields ---------------------------------------------------------------
    /**
     * Field description
     */
    private final Manager<T> manager;
}
