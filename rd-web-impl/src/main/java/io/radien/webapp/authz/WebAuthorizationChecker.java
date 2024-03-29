/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.service.PrincipalFactory;
import java.util.Optional;

import io.radien.webapp.JSFUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import static io.radien.api.service.permission.SystemPermissionsEnum.THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE;
import static io.radien.api.service.permission.SystemPermissionsEnum.THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE;

/**
 * Web Authorization checker validator
 */
@SessionScoped
@Named("authzChecker")
public class WebAuthorizationChecker extends AuthorizationChecker {

    private static final long serialVersionUID = -2440821831002121277L;
    @Inject
    HttpServletRequest servletRequest;

    @Inject
    UserSessionEnabled userSession;

    @Inject
    PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Override
    public HttpServletRequest getServletRequest() {
        return this.servletRequest;
    }

    private final transient Logger log = LoggerFactory.getLogger(WebAuthorizationChecker.class);

    /**
     * Will get the active user id
     * @return user id
     * @throws SystemException in case it founds any issue
     */
    @Override
    public Long getCurrentUserId() throws SystemException {
        Long id = userSession.getUserId();
        return id == null ? super.getCurrentUserId() : id;
    }

    /**
     * Returns the information of the active user
     * @return the active user
     */
    @Override
    protected SystemUser getInvokerUser() {
        return PrincipalFactory.create(userSession.getUserFirstName(),
                userSession.getUserLastName(), userSession.getPreferredUserName(),
                userSession.getUserIdSubject(), userSession.getEmail(), null);
    }
    @Override
    protected boolean isLoggedIn(){
        return userSession.isActive();
    }

    /**
     * Validates if current active user for a given tenant has a specific given role
     * @param tenantId Tenant identifier (Optional parameter)
     * @param roleName this parameter corresponds to the role name
     * @return true in case of user has role
     */
    @Override
    public boolean hasGrant(Long tenantId, String roleName) {
        try {
            return super.hasGrant(tenantId, roleName);
        }
        catch (Exception e) {
            log.error(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
            return false;
        }
    }

    /**
     * Check if user has access to a permission specified by the combination of params
     * resource and an action.
     * @param resource resource name
     * @param action action name
     * @param tenant tenant id (Optional)
     * @return true if the current logged user has grant to the permission, otherwise false
     */
    public boolean hasPermissionAccess(String resource, String action, Long tenant) {
        try {
            if(!isLoggedIn()){
                log.error("Checking if has permission without being logged in");
                return false;
            }
            boolean result = false;
            Optional<Long> idForAction = permissionRESTServiceAccess.getIdByResourceAndAction(resource, action);
            Optional<Long> idForAll = permissionRESTServiceAccess.getIdByResourceAndAction(resource, "ALL");

            if (idForAction.isPresent()) {
                 result = hasGrant(idForAction.get(), tenant);
            }
            if(!result && idForAll.isPresent()) {
                result = hasGrant(idForAll.get(), tenant);
            }
            log.info("Permission {} for resource {} and action {}", result ? "found" : "not found", resource, action);
            return result;
        }
        catch (Exception e) {
            log.error(GenericErrorCodeMessage.AUTHORIZATION_ERROR.toString(), e);
            return false;
        }
    }

    /**
     * Check if the current logged user has permission to reset password for any user
     * @param tenant check the permission under a particular tenant (Optional Parameter)
     * @return true if the current logged user has grant to do that, otherwise false
     */
    public boolean hasPermissionToResetPassword(Long tenant) {
        return hasPermissionAccess(THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getResource().getResourceName(),
                THIRD_PARTY_PASSWORD_MANAGEMENT_UPDATE.getAction().getActionName(), tenant);
    }

    /**
     * Check if the current logged user has permission to update email for any user
     * @param tenant check the permission under a particular tenant (Optional Parameter)
     * @return true if the current logged user has grant to do that, otherwise false
     */
    public boolean hasPermissionToUpdateUserEmail(Long tenant) {
        return hasPermissionAccess(THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getResource().getResourceName(),
                THIRD_PARTY_EMAIL_MANAGEMENT_UPDATE.getAction().getActionName(), tenant);
    }

    /**
     * Redirects the user if its not logged or if the user doesn't have permission
     * @param resource for check of the permission
     * @param action for check of the permission
     * @param tenant check the permission under a particular tenant (Optional Parameter)
     * @param prettyDestination destination of redirection
     * @return true if redirection occurs, otherwise false
     */
    public boolean redirectOnMissingPermission(String resource,String action,Long tenant,String prettyDestination){

        if(!userSession.isActive()) {
            log.error("Going to redirect Not Active");
            JSFUtil.redirect(prettyDestination);
            return true;
        }
        if(!hasPermissionAccess(resource, action, tenant)){
            log.error("Missing Permission Resource:{} Action:{} Tenant:{}",resource,action,tenant);
            JSFUtil.addErrorMessage("You dont have permission to access the requested page.");
            JSFUtil.redirect(prettyDestination);
            return true;
        }
        return false;
    }

}
