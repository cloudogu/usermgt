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

package de.triology.universeadm.group;

import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import de.triology.universeadm.PagedResultList;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("groups")
public class GroupResource extends AbstractManagerResource<Group> {

    private static final Logger logger = LoggerFactory.getLogger(GroupResource.class);

    private final GroupManager groupManager;
    private final UserManager userManager;

    private final UndeletableGroupManager undeletableGroupManager;

    @Inject
    public GroupResource(GroupManager groupManager, UserManager userManager, UndeletableGroupManager undeletableGroupManager) {
        super(groupManager);
        this.groupManager = groupManager;
        this.userManager = userManager;
        this.undeletableGroupManager = undeletableGroupManager;
    }

    @Override
    protected String getId(Group group) {
        return group.getName();
    }

    @Override
    protected void prepareForModify(String id, Group group) {
        group.setName(id);
    }

    @POST
    @Path("{name}/members/{member}")
    public Response addMember(@PathParam("name") String name, @PathParam("member") String member) {
        Response.ResponseBuilder builder;

        Group group = groupManager.get(name);
        User user = userManager.get(member);
        if (group == null) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } else if (user == null) {
            builder = Response.status(Response.Status.BAD_REQUEST);
        } else if (group.getMembers().contains(member)) {
            builder = Response.status(Response.Status.CONFLICT);
        } else {
            group.getMembers().add(member);
            groupManager.modify(group);
            builder = Response.noContent();
        }

        return builder.build();
    }

    @DELETE
    @Path("{name}/members/{member}")
    public Response removeMember(@PathParam("name") String name, @PathParam("member") String member) {
        Response.ResponseBuilder builder;

        Group group = groupManager.get(name);
        if (group == null) {
            builder = Response.status(Response.Status.NOT_FOUND);
        } else if (!group.getMembers().contains(member)) {
            builder = Response.status(Response.Status.CONFLICT);
        } else {
            group.getMembers().remove(member);
            groupManager.modify(group);
            builder = Response.noContent();
        }

        return builder.build();
    }

    @GET
    @Path("undeletable")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUndeletable() {
        Response.ResponseBuilder builder;
        try {
            List<String> groups = undeletableGroupManager.getNonDeleteClassList();
            builder = Response.ok(groups, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error("call /api/groups/undeletable without prior authentication");
            builder = Response.status(Response.Status.BAD_REQUEST);
        }
        return builder.build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("start") int s, @QueryParam("limit") int l, @QueryParam("query") String query, @QueryParam("exclude") final String exclude) {
        Response.ResponseBuilder builder;
        try {
            final List<String> excludedGroups = Arrays
                    .asList(exclude.split(","));

            final PagedResultList<Group> result = groupManager
                    .search(query, AbstractManagerResource.PAGING_DEFAULT_START, AbstractManagerResource.PAGING_MAXIMUM);

            final List<Group> filtered = result
                    .getEntries()
                    .stream()
                    .filter(g -> !excludedGroups.contains(g.getName()))
                    .collect(Collectors.toList());

            final List<Group> limited = filtered.stream().limit(l).collect(Collectors.toList());

            final PagedResultList<Group> paged = new PagedResultList<>(
                    limited,
                    AbstractManagerResource.PAGING_DEFAULT_START,
                    AbstractManagerResource.PAGING_MAXIMUM,
                    limited.size()
            );

            builder = Response.ok(paged, MediaType.APPLICATION_JSON);
        } catch (Exception e) {
            logger.error("call /api/groups/filtered failed: " + e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST);
        }

        return builder.build();
    }
}
