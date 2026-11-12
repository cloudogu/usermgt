package de.triology.universeadm.dogu;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("dogus")
public class DoguResource {
    private static final Logger logger = LoggerFactory.getLogger(DoguResource.class);
    private final DoguProvider doguProvider;

    @Inject
    public DoguResource(DoguProvider doguProvider) {
        this.doguProvider = doguProvider;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDogus(@QueryParam("systemdogus") @DefaultValue("true") boolean systemdogus) {
        List<Dogu> dogus = doguProvider.getDogus();
        if (!systemdogus) {
            // Like the admin dogu, classify dogus without the warp tag as system dogus.
            dogus = dogus.stream()
                    .filter(dogu -> dogu.getTags() != null && dogu.getTags().contains("warp"))
                    .collect(Collectors.toList());
        }
        for (Dogu dogu : dogus) {
            logger.debug("GET /dogus response entry: name='{}', displayName='{}', category='{}', tags={}",
                    dogu.getName(), dogu.getDisplayName(), dogu.getCategory(), dogu.getTags());
        }
        return Response.ok(dogus).build();
    }
}
