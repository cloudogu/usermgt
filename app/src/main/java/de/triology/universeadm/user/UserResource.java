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


package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.user.imports.*;
import org.apache.shiro.authz.AuthorizationException;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.function.BiFunction;


/**
 * TODO remove package cycle with group.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("users")
public class UserResource extends AbstractManagerResource<User> {
    /**
     * Constructs ...
     *
     * @param userManager
     * @param groupManager
     */
    @Inject
    public UserResource(UserManager userManager, GroupManager groupManager, CSVHandler csvHandler) {
        super(userManager);
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.csvHandler = csvHandler;
    }

    //~--- methods --------------------------------------------------------------
    @POST
    @Path(":import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importUsers(MultipartFormDataInput input) {
        logger.debug("Received csv import request.");
        BiFunction<Response.Status, String, Response> createError = (status, errMsg) -> Response
                        .status(status)
                        .entity(errMsg)
                        .build();

        try {
            Result result = this.csvHandler.handle(input);
            logger.debug("Successfully handled csv import {}", result);

            return Response.status(Response.Status.OK).entity(result).build();
        } catch (BadArgumentException e) {
            logger.error("Bad input while handling csv user import", e);
            String errMsg = e.getPublicErrMsg().isEmpty() ? e.getMessage() : e.getPublicErrMsg();

            return createError.apply(Response.Status.BAD_REQUEST, errMsg);
        } catch (AuthorizationException e) {
            logger.error("Missing privileges while handling csv user import");

            return createError.apply(Response.Status.FORBIDDEN, "Missing privileges to use import");
        }
    }


    /**
     * Method description
     *
     * @param username
     * @param groupname
     * @return
     */
    @POST
    @Path("{user}/groups/{group}")
    public Response addMembership(@PathParam("user") String username,
                                  @PathParam("group") String groupname) {
        Response.ResponseBuilder builder;
        User user = userManager.get(username);
        Group group = groupManager.get(groupname);

        if (user == null) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } else if (group == null) {
            builder = Response.status(Response.Status.BAD_REQUEST);
        } else if (user.getMemberOf().contains(groupname)) {
            builder = Response.status(Response.Status.CONFLICT);
        } else {
            user.getMemberOf().add(groupname);
            userManager.modify(user);
            builder = Response.noContent();
        }

        return builder.build();
    }

    /**
     * Method description
     *
     * @param username
     * @param group
     * @return
     */
    @DELETE
    @Path("{user}/groups/{group}")
    public Response removeMember(@PathParam("user") String username,
                                 @PathParam("group") String group) {
        Response.ResponseBuilder builder;

        User user = userManager.get(username);

        if (user == null) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } else if (!user.getMemberOf().contains(group)) {
            builder = Response.status(Response.Status.CONFLICT);
        } else {
            user.getMemberOf().remove(group);
            userManager.modify(user);
            builder = Response.noContent();
        }
        return builder.build();
    }

    /**
     * Method description
     *
     * @param id
     * @param user
     */
    @Override
    protected void prepareForModify(String id, User user) {
        user.setUsername(id);
    }

    //~--- get methods ----------------------------------------------------------

    /**
     * Method description
     *
     * @param user
     * @return
     */
    @Override
    protected String getId(User user) {
        return user.getUsername();
    }

    //~--- fields ---------------------------------------------------------------

    private static final Logger logger =
            LoggerFactory.getLogger(UserResource.class);

    /**
     * Field description
     */
    private final GroupManager groupManager;

    /**
     * Field description
     */
    private final UserManager userManager;

    /**
     * Field description
     */
    private final CSVHandler csvHandler;
}
