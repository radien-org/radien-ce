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
package io.radien.ms.openid.security;

import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.openid.entities.Principal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.io.Serializable;
import java.util.Optional;

/**
 * This abstract class maybe extended by any component that needs to propagate
 * access token information, retrieve information regarding the current logged user and
 * assess authorization (Role, permission, etc)
 */
public abstract class AuthorizationChecker extends TokensPropagator {

    private String accessToken;
    private String refreshToken;

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private UserRESTServiceAccess userRESTServiceAccess;
    @Inject
    private LinkedAuthorizationRESTServiceAccess authorizationRESTServiceAccess;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean hasGrant(Long tenantId, String roleName) throws SystemException{
        try {
            this.preProcess();
            return authorizationRESTServiceAccess.isRoleExistentForUser(
                    getCurrentUserId(), tenantId, roleName);
        } catch (Exception e) {
            this.log.error("Error checking authorization", e);
            throw new SystemException(e);
        }
    }

    public boolean hasGrant(String roleName) throws SystemException{
        return hasGrant(null, roleName);
    }

    public boolean hasGrant(Long permissionId, Long tenantId) throws SystemException {
        try {
            this.preProcess();
            return authorizationRESTServiceAccess.checkIfLinkedAuthorizationExists(tenantId,
                    permissionId, null, getCurrentUserId());
        } catch (Exception e) {
            this.log.error("Error checking authorization", e);
            throw new SystemException(e);
        }
    }

    protected Long getCurrentUserIdBySub(String sub) throws SystemException {
        Optional<SystemUser> optional = this.userRESTServiceAccess.getUserBySub(sub);
        return optional.orElseThrow(() -> new SystemException("No user available for " + sub)).getId();
    }

    protected Long getCurrentUserId() throws SystemException {
        SystemUser user = getInvokerUser();
        if (user == null) {
            throw new SystemException("No current user available");
        }
        return getCurrentUserIdBySub(user.getSub());
    }

    public LinkedAuthorizationRESTServiceAccess getAuthorizationRESTServiceAccess() {
        return authorizationRESTServiceAccess;
    }

    public UserRESTServiceAccess getUserRESTServiceAccess() {
        return userRESTServiceAccess;
    }
}
