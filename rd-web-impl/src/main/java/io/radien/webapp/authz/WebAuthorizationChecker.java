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
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.service.PrincipalFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

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
        SystemUser user = PrincipalFactory.create(userSession.getUserFirstName(),
                userSession.getUserLastName(), userSession.getPreferredUserName(),
                userSession.getUserIdSubject(), userSession.getEmail(), null);
        return user;
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
            log.error("Error checking authorization", e);
            return false;
        }
    }

    /**
     * Validates if the current user has any of the multiple correct roles given
     * System Administrator or User AdministratorhasUserAdministratorRoleAccess
     * @return true if user has one of those
     */
    public boolean hasUserAdministratorRoleAccess() {
        try {
            List<String> roleNames = new ArrayList<>();
            roleNames.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
            roleNames.add(SystemRolesEnum.USER_ADMINISTRATOR.getRoleName());
            return super.hasGrantMultipleRoles(roleNames);
        }
        catch (Exception e) {
            log.error("Error checking authorization", e);
            return false;
        }
    }

    /**
     * Verifies if the current logged user has relevant roles to perform actions
     * regarding tenant administration. It includes System Administrator, Tenant Administrator
     * Client Tenant Administrator and Sub Tenant Administrator
     * @return true if user has some of these roles, otherwise false
     */
    public boolean hasTenantAdministratorRoleAccess() {
        try {
            List<String> roleNames = new ArrayList<>();
            roleNames.add(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName());
            roleNames.add(SystemRolesEnum.TENANT_ADMINISTRATOR.getRoleName());
            roleNames.add(SystemRolesEnum.CLIENT_TENANT_ADMINISTRATOR.getRoleName());
            roleNames.add(SystemRolesEnum.SUB_TENANT_ADMINISTRATOR.getRoleName());
            return super.hasGrantMultipleRoles(roleNames);
        }
        catch (Exception e) {
            log.error("Error checking authorization", e);
            return false;
        }
    }
}
