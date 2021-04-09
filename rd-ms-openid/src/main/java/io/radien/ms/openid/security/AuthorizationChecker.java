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
//import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
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
 * TokenPlaceHolder implementation to be extend by any
 * Service that requires AccessToken
 */
public abstract class AuthorizationChecker implements TokensPlaceHolder, Serializable {

    private String accessToken;
    private String refreshToken;

    @Context
    private HttpServletRequest servletRequest;

    @Inject
    private UserRESTServiceAccess userRESTServiceAccess;

    //@Inject
    //private LinkedAuthorizationRESTServiceAccess authorizationRESTServiceAccess;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean hasGrant(Long tenantId, String roleName) throws SystemException{
        try {
            this.preProcess();
            return false;//authorizationRESTServiceAccess.isRoleExistentForUser(
                    //getCurrentUserId(), tenantId, roleName);
        } catch (Exception e) {
            this.log.error("Error checking authorization", e);
            throw new SystemException(e);
        }
    }

    public boolean hasGrant(String roleName) throws SystemException{
        return hasGrant(null, roleName);
    }

    public boolean hasGrant(Long permissionId, Long roleId, Long tenantId) throws SystemException {
        try {
            this.preProcess();
            //return authorizationRESTServiceAccess.checkIfLinkedAuthorizationExists(tenantId,
            //        permissionId, roleId, getCurrentUserId());
            return false;
        } catch (Exception e) {
            this.log.error("Error checking authorization", e);
            throw new SystemException(e);
        }
    }

    protected Long getCurrentUserId() throws SystemException {
        SystemUser user = (io.radien.ms.usermanagement.client.entities.User)
                servletRequest.getSession().getAttribute("USER");
        if (user == null) {
            throw new SystemException("No current user available");
        }
        return getCurrentUserIdBySub(user.getSub());
    }

    protected Long getCurrentUserIdBySub(String sub) throws SystemException {
        Optional<SystemUser> optional = this.userRESTServiceAccess.getUserBySub(sub);
        return optional.orElseThrow(() -> new SystemException("No user available for " + sub)).getId();
    }

    protected void preProcess() {
        HttpSession httpSession = this.servletRequest.getSession(false);
        if (httpSession.getAttribute("accessToken") != null &&
                httpSession.getAttribute("refreshToken") != null) {
            this.accessToken = httpSession.getAttribute("accessToken").toString();
            this.refreshToken = httpSession.getAttribute("refreshToken").toString();
        }
        else {
            // Lets obtain (at least) accessToken from Header
            String token = this.servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null && token.startsWith("Bearer ")) {
                this.accessToken = token.substring(7);
            }
        }
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }
}