/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.rolemanagement.services;

import io.radien.api.SystemVariables;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.ms.tenantmanagement.client.exceptions.InternalServerErrorException;
import java.util.Optional;
import javax.inject.Inject;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_ASSOCIATION_TENANT_ROLE;

/**
 * Abstract class that covers the common features/methods to be used
 * by TenantRole, TenantRoleUser and TenantRolePermission
 */
public abstract class AbstractTenantRoleDomainBusinessService {

    @Inject
    private TenantRoleServiceAccess tenantRoleServiceAccess;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Inject
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Inject
    private RoleServiceAccess roleServiceAccess;

    /**
     * Utility method to retrieve tenant and reduce cognitive complexity
     * @param tenantId tenant identifier
     * @return instance of SystemTenant
     * @throws SystemException in case of communication issues with tenant rest client
     * @throws TenantRoleException if tenant could not be found for the informed id
     */
    protected SystemTenant retrieveTenant(Long tenantId) throws SystemException, TenantRoleException {
        Optional<SystemTenant> optionalSystemTenant;
        try {
            optionalSystemTenant = tenantRESTServiceAccess.getTenantById(tenantId);
        }
        catch(NotFoundException n) {
            optionalSystemTenant = Optional.empty();
        }
        catch (InternalServerErrorException | SystemException s) {
            throw new SystemException(s);
        }
        return optionalSystemTenant.orElseThrow(() -> new TenantRoleException(GenericErrorCodeMessage.
                TENANT_ROLE_NO_TENANT_FOUND.toString(String.valueOf(tenantId))));
    }

    /**
     * Check if all specified parameters were informed
     * @param params vars contains variables that correspond method parameters
     */
    protected void checkIfMandatoryParametersWereInformed(Object...params) {
        for (Object o:params) {
            if (o == null)
                throw new IllegalArgumentException("One mandatory parameter not informed");
        }
    }

    /**
     * Given a tenant and a role, retrieves the existent id for such association
     * @param tenant tenant identifier (id)
     * @param role role identifier (id)
     * @return the association id (if exists), otherwise throws a exception
     * @throws TenantRoleNotFoundException thrown if association does not exists
     * @throws TenantRoleIllegalArgumentException thrown when either tenant or role are not informed
     */
    public Long getTenantRoleId(Long tenant, Long role) throws TenantRoleNotFoundException, TenantRoleIllegalArgumentException{
        if (tenant == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }
        if (role == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_ID.getLabel()));
        }
        return this.getTenantRoleServiceAccess().getTenantRoleId(tenant, role).
                orElseThrow(() -> new TenantRoleNotFoundException(TENANT_ROLE_ASSOCIATION_TENANT_ROLE.toString(
                        tenant.toString(), role.toString())));
    }

    /**
     * Check if all the informed Params (Tenant, Role, etc) exist
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @param permissionId Permission identifier
     * @throws SystemException in case of error during communication process using REST Clients
     * @throws TenantRoleIllegalArgumentException in case of any inconsistency (ex: tenant not found for the informed id)
     */
    protected void checkIfParamsExists(Long tenantId, Long roleId, Long permissionId)
            throws SystemException, TenantRoleException {
        if (tenantId != null && !getTenantRESTServiceAccess().isTenantExistent(tenantId)) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_NO_TENANT_FOUND.toString(String.valueOf(tenantId)));
        }
        if (permissionId != null) {
            try {
                permissionRESTServiceAccess.isPermissionExistent(permissionId, null);
            } catch (NotFoundException nfe) {
                throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                        TENANT_ROLE_NO_PERMISSION_FOUND.toString(String.valueOf(permissionId)));
            }
        }
        if (roleId != null && !this.roleServiceAccess.checkIfRolesExist(roleId, null) ) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_NO_ROLE_FOUND.toString(String.valueOf(roleId)));
        }
    }

    /**
     * Getter for the property {@link TenantRoleBusinessService#tenantRESTServiceAccess}
     * @return tenantRESTServiceAccess instance of TenantRESTServiceAccess
     */
    public TenantRESTServiceAccess getTenantRESTServiceAccess() {
        return tenantRESTServiceAccess;
    }

    /**
     * Setter for the property {@link TenantRoleBusinessService#tenantRESTServiceAccess}
     * @param tenantRESTServiceAccess instance of TenantRESTServiceAccess to be set
     */
    public void setTenantRESTServiceAccess(TenantRESTServiceAccess tenantRESTServiceAccess) {
        this.tenantRESTServiceAccess = tenantRESTServiceAccess;
    }

    /**
     * Getter for the property {@link TenantRoleBusinessService#permissionRESTServiceAccess}
     * @return instance of PermissionRESTServiceAccess
     */
    public PermissionRESTServiceAccess getPermissionRESTServiceAccess() {
        return permissionRESTServiceAccess;
    }

    /**
     * Setter for the property {@link TenantRoleBusinessService#permissionRESTServiceAccess}
     * @param permissionRESTServiceAccess instance of PermissionRESTServiceAccess to be set
     */
    public void setPermissionRESTServiceAccess(PermissionRESTServiceAccess permissionRESTServiceAccess) {
        this.permissionRESTServiceAccess = permissionRESTServiceAccess;
    }

    /**
     * Getter for the property {@link TenantRoleBusinessService#roleServiceAccess}
     * @return instance of RoleServiceAccess
     */
    public RoleServiceAccess getRoleServiceAccess() {
        return roleServiceAccess;
    }

    /**
     * Setter for the property {@link TenantRoleBusinessService#roleServiceAccess}
     * @param roleServiceAccess instance of RoleServiceAccess
     */
    public void setRoleServiceAccess(RoleServiceAccess roleServiceAccess) {
        this.roleServiceAccess = roleServiceAccess;
    }

    /**
     * Getter for the property {@link TenantRoleBusinessService#tenantRoleServiceAccess}
     * @return instance of tenantRoleServiceAccess
     */
    public TenantRoleServiceAccess getTenantRoleServiceAccess() {
        return tenantRoleServiceAccess;
    }

    /**
     * Getter for the property {@link TenantRoleBusinessService#tenantRoleServiceAccess}
     * @param tenantRoleServiceAccess instance of tenantRoleServiceAccess
     */
    public void setTenantRoleServiceAccess(TenantRoleServiceAccess tenantRoleServiceAccess) {
        this.tenantRoleServiceAccess = tenantRoleServiceAccess;
    }
}
