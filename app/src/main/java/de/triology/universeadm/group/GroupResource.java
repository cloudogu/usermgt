package de.triology.universeadm.group;

import com.google.inject.Inject;
import de.triology.universeadm.AbstractManagerResource;
import de.triology.universeadm.user.User;
import de.triology.universeadm.user.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
  protected String getDefaultSortAttribute() {
    return "name";
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
}
