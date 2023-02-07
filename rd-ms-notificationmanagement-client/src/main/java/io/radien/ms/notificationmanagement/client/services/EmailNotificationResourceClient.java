package io.radien.ms.notificationmanagement.client.services;

import io.radien.ms.openid.entities.GlobalHeaders;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

@Path("email")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface EmailNotificationResourceClient {

    @POST
    Response notify(@QueryParam("email") String email, @QueryParam("viewId") String notificationViewId, @QueryParam("language") String language, Map<String, String> args);

    @POST
    @Path("/users")
    Response notifyUsers(@QueryParam("userIds") List<Long> userIds, @QueryParam("viewId") String notificationViewId, @QueryParam("language") String language, Map<String, String> args);
}
