/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.webapp.authz;

import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.entities.Principal;
import io.radien.ms.openid.service.PrincipalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Logger log = LoggerFactory.getLogger(WebAuthorizationChecker.class);

    @Override
    protected Long getCurrentUserId() throws SystemException {
        Long id = userSession.getUserId();
        return id == null ? super.getCurrentUserId() : id;
    }

    @Override
    protected SystemUser getInvokerUser() {
        SystemUser user = PrincipalFactory.create(userSession.getUserFirstName(),
                userSession.getUserLastName(), userSession.getPreferredUserName(),
                userSession.getUserIdSubject(), userSession.getEmail(), null);
        return user;
    }

    @Override
    public boolean hasGrant(Long tenantId, String roleName) {
        try {
            return super.hasGrant(tenantId, roleName);
        }
        catch (Exception e) {
            log.error("Error checking authorization", e);
            return false;
        }
    }
}
