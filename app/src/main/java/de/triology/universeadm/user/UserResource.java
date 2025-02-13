package de.triology.universeadm.user;

//~--- non-JDK imports --------------------------------------------------------

import com.google.inject.Inject;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.triology.universeadm.AbstractManagerResource;
import de.triology.universeadm.PaginationQuery;
import de.triology.universeadm.PaginationResult;
import de.triology.universeadm.PaginationResultResponse;
import de.triology.universeadm.group.Group;
import de.triology.universeadm.group.GroupManager;
import de.triology.universeadm.user.imports.*;
import de.triology.universeadm.UniqueConstraintViolationResponse;
import de.triology.universeadm.UniqueConstraintViolationException;
import de.triology.universeadm.FieldConstraintViolationResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.shiro.authz.AuthorizationException;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.opensaml.artifact.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * TODO remove package cycle with group.
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
@Path("users")
public class UserResource extends AbstractManagerResource<User> {
    final int PAGING_DEFAULT_SUMMARY_LIMIT = 10;
    /**
     * Constructs ...
     *
     * @param userManager
     * @param groupManager
     */
    @Inject
    public UserResource(UserManager userManager, GroupManager groupManager, ImportHandler importHandler) {
        super(userManager);
        this.userManager = userManager;
        this.groupManager = groupManager;
        this.importHandler = importHandler;
    }

    //~--- methods --------------------------------------------------------------

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Context UriInfo uriInfo, User object) {
        // if the request contains a sync param the users are created synchronously
        // this fixes an LDAP issue where too many requests cause inconsistent data
        // Warning: this significantly slows down the user creation
        if (uriInfo.getQueryParameters().containsKey("sync") && uriInfo.getQueryParameters().getFirst("sync").equals("true")) {
                logger.info("Creating user synchronous");
                Response.ResponseBuilder builder;

                String id = getId(object);
                try {
                    userManager.createSynced(object);

                    UriBuilder uriBuilder = UriBuilder.fromUri(uriInfo.getRequestUri());

                    uriBuilder.path(id);
                    builder = Response.created(uriBuilder.build());
                } catch (UniqueConstraintViolationException e) {
                    logger.warn(CONSTRAINT_VIOLATION_LOG_MSG, id);
                    builder = Response.status(Response.Status.CONFLICT).entity(new UniqueConstraintViolationResponse(e));
                } catch (FieldConstraintViolationException e) {
                    logger.warn(CONSTRAINT_VIOLATION_LOG_MSG, id);
                    builder = Response.status(Response.Status.CONFLICT).entity(new FieldConstraintViolationResponse(e));
                }

                return builder.build();
            } else {
            logger.info("Creating user asynchronous");
            return super.create(uriInfo, object);
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

    /**
     * Method description
     *
     * @return
     */
    @Override
    protected String getDefaultSortAttribute() {
        return "username";
    }

    //~--- import methods ----------------------------------------------------------

    @POST
    @Path("import")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importUsers(MultipartFormDataInput input) {
        logger.debug("Received csv import request.");

        try {
            Result result = this.importHandler.handle(input);
            logger.debug("Successfully handled csv import {}", result);

            return Response.status(Response.Status.OK).entity(result).build();
        } catch (CsvRequiredFieldEmptyException e) {
            List<String> affectedColumns = e.getDestinationFields().stream()
                    .map(Field::getName)
                    .collect(Collectors.toList());;

            ImportError error = new ImportError.Builder(ImportError.Code.MISSING_FIELD_ERROR)
                    .withLineNumber(0)
                    .withErrorMessage(e.getMessage())
                    .withAffectedColumns(affectedColumns)
                    .build();

            logger.error("Invalid header when parsing csv file", e);

            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (InvalidArgumentException e) {
            logger.error("Bad input while handling csv user import", e);

            ImportError error = new ImportError.Builder(ImportError.Code.MISSING_FIELD_ERROR)
                    .withErrorMessage(e.getMessage())
                    .build();

            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } catch (AuthorizationException e) {
            logger.error("Missing privileges while handling csv user import");

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("Missing privileges to use import")
                    .build();

        } catch (RuntimeException | IOException e) {
            logger.error("Unexpected internal exception", e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("import/{importID}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportResult(@PathParam("importID") String importID) {
        logger.debug("Received get request for import result with ID {}", importID);

        try {
            UUID importUUID = UUID.fromString(importID);
            Result result = this.importHandler.getResult(importUUID);

            return Response.status(Response.Status.OK).entity(result).build();
        } catch (IllegalArgumentException e) {
            logger.warn("GET request for import result with invalid UUID {}", importID, e);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Invalid UUIDv4 for importID")
                    .build();
        } catch (FileNotFoundException e) {
            logger.warn("Could not find import result for ID {}", importID, e);
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Requested result not available")
                    .build();
        }
    }

    @GET
    @Path("import/{importID}/download")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportResultBinary(@PathParam("importID") String importID) {
        logger.debug("Received get request for downloading the import result with ID {}", importID);

        Response response = this.getImportResult(importID);

        if (response.getStatus() != Response.Status.OK.getStatusCode()){
            return response;
        }

        String fileName = String.format("%s.json", importID);
        response.getHeaders().putSingle("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));

        return response;
    }

    @DELETE
    @Path("import/{importID}")
    public Response deleteImportResult(@PathParam("importID") String importID) {
        logger.debug("Received delete request for import result with ID {}", importID);

        try {
            UUID importUUID = UUID.fromString(importID);
            boolean result = this.importHandler.deleteResult(importUUID);

            if(result) {
                logger.info("ImportFile with ID {} has been deleted.", importID);
            } else {
                logger.warn("Could not delete import file with ID {}, because it has not been found", importID);
            }

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            logger.warn("DELETE request for import result with invalid UUID {}", importID, e);
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("Invalid UUIDv4 for importID")
                    .build();
        } catch (IOException e) {
            logger.error("Could not delete import file with ID {}", importID, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


  @GET
  @Path("import/summaries")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSummaries(@QueryParam("page") int page, @QueryParam("page_size") int pageSize) {
    logger.debug("Received request get all summaries");

    try {
      PaginationQuery query = new PaginationQuery(page, pageSize);

      Pair<List<Result.Summary>, Integer> summaryResult = this.importHandler.getSummaries(query.getOffset(), query.getPageSize());
      List<Result.Summary> summaries = summaryResult.getLeft();
      int totalSummaryCount = summaryResult.getRight();
      PaginationResult<Result.Summary> result = new PaginationResult<>(summaries, totalSummaryCount, null);

      PaginationResultResponse<Result.Summary> response = new PaginationResultResponse<>(query, result, getCurrentPath());

      return Response.status(Response.Status.OK).entity(response).build();
    } catch (IOException e) {
      logger.error("Unable to read summaries from system", e);

      return Response
        .status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity("Failure reading summaries")
        .build();
    }
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
    private final ImportHandler importHandler;
}
