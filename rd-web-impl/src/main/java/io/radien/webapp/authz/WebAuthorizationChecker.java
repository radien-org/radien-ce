package io.radien.webapp.authz;

import io.radien.api.OAFAccess;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.openid.service.PrincipalFactory;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

@SessionScoped
@Named("authzChecker")
public class WebAuthorizationChecker extends AuthorizationChecker {
    @Inject
    HttpServletRequest servletRequest;

    @Inject
    UserSessionEnabled userSession;

    @Override
    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }

    @Override
    protected SystemUser getInvokerUser() {
        SystemUser user = PrincipalFactory.create(userSession.getUserFirstName(),
                userSession.getUserLastName(), userSession.getPreferredUserName(),
                userSession.getUserIdSubject(), userSession.getEmail(), null);
        return user;
    }
}
