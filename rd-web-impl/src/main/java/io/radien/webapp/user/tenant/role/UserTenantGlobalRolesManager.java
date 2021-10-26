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
package io.radien.webapp.user.tenant.role;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.role.SystemRolesEnum;

import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.authz.WebAuthorizationChecker;

import java.util.Optional;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;

import javax.inject.Inject;
/**
 * JSF manager bean that handle the Global Role(s) for a User
 * Under the Root Tenant
 *
 * @author Rajesh Gavvala
 */
@Model
@RequestScoped
public class UserTenantGlobalRolesManager extends AbstractManager {
    private static final long serialVersionUID = 6400501242523402755L;

    @Inject
    private WebAuthorizationChecker webAuthorizationChecker;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    private SystemActiveTenant systemActiveTenant;
    private boolean roleAdministratorInRootContext = false;

    /**
     * Validates RootTenantUserRoleAdministratorAccess
     * @return boolean value if User belongs to ROOT TENANT and
     * Has Role Administrator privileges
     */
    public boolean isRoleAdministratorInRootContext() {
        getSystemActiveTenant();
        SystemTenant systemTenant = null;
        try{
            if(systemActiveTenant != null){
                Optional<SystemTenant> optionalSystemTenant = tenantRESTServiceAccess.getTenantById(systemActiveTenant.getId());
                if(optionalSystemTenant.isPresent()){
                    systemTenant = optionalSystemTenant.get();
                }

                if(systemTenant != null && systemTenant.getTenantType().equals(TenantType.ROOT)){
                    roleAdministratorInRootContext = webAuthorizationChecker
                            .hasGrant(systemActiveTenant.getId(), SystemRolesEnum.ROLE_ADMINISTRATOR.getRoleName());
                }
            }
        } catch (Exception e){
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.USER_RD_ROOT_TENANT_ROLE_ADMINISTRATOR_ACCESS_ERROR.getValue()));
        }
        return roleAdministratorInRootContext;
    }

    /**
     * Sets RootTenantUserRoleAdministratorAccess boolean property
     * @param roleAdministratorInRootContext to be set
     */
    public void setRoleAdministratorInRootContext(boolean roleAdministratorInRootContext) {
        this.roleAdministratorInRootContext = roleAdministratorInRootContext;
    }

    /**
     * Gets Active Tenant
     * @return System Active Tenant
     */
    public SystemActiveTenant getSystemActiveTenant() {
        if(activeTenantDataModelManager.isTenantActive()){
            systemActiveTenant = activeTenantDataModelManager.getActiveTenant();
        }
        return systemActiveTenant;
    }

    /**
     * Sets System Active Tenant
     * @param systemActiveTenant to set
     */
    public void setSystemActiveTenant(SystemActiveTenant systemActiveTenant) {
        this.systemActiveTenant = systemActiveTenant;
    }
}