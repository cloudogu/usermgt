package de.triology.universeadm.dogu;

import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("dogus")
public class DoguResource {
    private final DoguProvider doguProvider;

    @Inject
    public DoguResource(DoguProvider doguProvider) {
        this.doguProvider = doguProvider;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDogus() {
        return Response.ok(doguProvider.getDogus()).build();
    }
}
