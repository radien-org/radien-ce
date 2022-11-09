package io.radien.ms.notificationmanagement.resource;

import io.radien.api.service.notification.email.EmailNotificationBusinessServiceAccess;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.notificationmanagement.client.services.EmailNotificationResourceClient;
import io.radien.ms.openid.entities.Authenticated;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("email")
@RequestScoped
@Authenticated
public class EmailNotificationResource extends AuthorizationChecker implements EmailNotificationResourceClient {
    @Inject
    private EmailNotificationBusinessServiceAccess emailNotificationService;

    @POST
    public Response notifyCurrentUser(@QueryParam("viewId")String notificationViewId, @QueryParam("language") String language, Map<String, String> args) {
        return Response.ok(emailNotificationService.notifyUser(getInvokerUser(), notificationViewId, language, args)).build();
    }

}
