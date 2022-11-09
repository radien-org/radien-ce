package io.radien.ms.notificationmanagement.client.services;

import io.radien.ms.openid.entities.GlobalHeaders;
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
    Response notifyCurrentUser(@QueryParam("viewId")String notificationViewId, @QueryParam("language") String language, Map<String, String> args);
}
