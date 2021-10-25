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
package io.radien.ms.rolemanagement.services;

import io.radien.api.SystemVariables;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE;

/**
 * Component that orchestrates the using of diverse Service Access components
 * to handle TenantRole (Permission) domain business rules
 * @author Newton Carvalho
 */
@Stateless
public class TenantRolePermissionBusinessService extends AbstractTenantRoleDomainBusinessService implements Serializable {

    @Inject
    private TenantRolePermissionServiceAccess tenantRolePermissionService;

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantRolePermission Association among Tenant, Role and Permission (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     * @throws UniquenessConstraintException in case of error during the insertion
     * @throws SystemException in case of communication issues with REST client
     */
    public void assignPermission(TenantRolePermissionEntity tenantRolePermission) throws TenantRoleException,
            UniquenessConstraintException, SystemException {
        if (tenantRolePermission.getPermissionId() == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel()));
        }
        checkIfParamsExists(null, null, tenantRolePermission.getPermissionId());
        SystemTenantRole tenantRole = getTenantRoleServiceAccess().get(tenantRolePermission.getTenantRoleId());
        if (tenantRole == null) {
            throw new TenantRoleNotFoundException(TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(String.valueOf(
                    tenantRolePermission.getTenantRoleId())));
        }
        if (this.tenantRolePermissionService.isAssociationAlreadyExistent(tenantRolePermission.getPermissionId(),
                tenantRolePermission.getTenantRoleId())) {
            throw new TenantRoleException(TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE.
                    toString(String.valueOf(tenantRole.getTenantId()), String.valueOf(tenantRole.getRoleId())));
        }
        this.tenantRolePermissionService.create(tenantRolePermission);
    }

    /**
     * Update a TenantRolePermission (In other words, updates a permission
     * assignment done previously)
     * @param tenantRolePermission Association among Tenant, Role and Permission (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     * @throws UniquenessConstraintException in case of error during the insertion
     * @throws SystemException in case of communication issues with REST client
     */
    public void update(TenantRolePermissionEntity tenantRolePermission) throws TenantRoleException,
            UniquenessConstraintException, SystemException {

        checkPermissionId(tenantRolePermission);
        checkIfParamsExists(null, null, tenantRolePermission.getPermissionId());
        SystemTenantRole tenantRole = getTenantRoleServiceAccess().get(tenantRolePermission.getTenantRoleId());
        if (tenantRole == null) {
            throw new TenantRoleNotFoundException(TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(String.valueOf(
                    tenantRolePermission.getTenantRoleId())));
        }
        if (this.tenantRolePermissionService.isAssociationAlreadyExistent(tenantRolePermission.getPermissionId(),
                tenantRolePermission.getTenantRoleId(), tenantRolePermission.getId())) {
            throw new TenantRoleException(TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE.
                    toString(String.valueOf(tenantRole.getTenantId()), String.valueOf(tenantRole.getRoleId())));
        }
        this.tenantRolePermissionService.update(tenantRolePermission);
    }

    /**
     * Check if TenantRolePermission pojo contains permission id (Avoid code duplication)
     * @param tenantRolePermission {@link TenantRolePermissionEntity} instance to be checked
     * @throws TenantRoleIllegalArgumentException in case of not informed permission id
     */
    private void checkPermissionId(TenantRolePermissionEntity tenantRolePermission) throws TenantRoleIllegalArgumentException {
        if (tenantRolePermission.getPermissionId() == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel()));
        }
    }

    /**
     * Deletes a Tenant Role Permission association
     * @param id Tenant Role Id association Identifier
     * @return If the association was found and the delete process was successfully performed
     * @throws TenantRoleException If does not exist Tenant Role for the given id, or Tenant Role exists but
     * is linked with other Entities like Tenant Role Permission or Tenant Role User (so, it could not be removed)
     */
    public boolean delete(Long id) throws TenantRoleException {
        SystemTenantRolePermission systemTenantRolePermission = this.getTenantRolePermissionService().get(id);
        if (systemTenantRolePermission == null) {
            throw new TenantRoleNotFoundException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_PERMISSION_FOUND.toString(id.toString()));
        }
        return this.getTenantRolePermissionService().delete(id);
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param permission User identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     */
    public void unAssignPermission(Long tenant, Long role, Long permission) throws TenantRoleException{
        if (tenant == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }
        if (role == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.ROLE_ID.getLabel()));
        }
        if (permission == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel()));
        }
        Long tenantRoleId = getTenantRoleId(tenant, role);
        Long tenantRolePermissionId = this.tenantRolePermissionService.
                getTenantRolePermissionId(tenantRoleId, permission).orElseThrow(() -> new TenantRoleException(
                        TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION.toString(permission.toString())));
        this.tenantRolePermissionService.delete(tenantRolePermissionId);
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions
     */
    public List<SystemPermission> getPermissions(Long tenantId, Long roleId, Long userId) throws SystemException {
        checkIfMandatoryParametersWereInformed(tenantId, roleId);
        List<SystemPermission> list = new ArrayList<>();
        List<Long> ids = this.getTenantRoleServiceAccess().getPermissions(tenantId, roleId, userId);
        if (!ids.isEmpty()) {
            list.addAll(this.getPermissionRESTServiceAccess().getPermissionsByIds(ids));
        }
        return list;
    }

    /**
     * Getter for the property {@link TenantRolePermissionBusinessService#tenantRolePermissionService}
     * @return instance of Tenant Role Permission Service
     */
    public TenantRolePermissionServiceAccess getTenantRolePermissionService() {
        return tenantRolePermissionService;
    }

    /**
     * Setter for the property {@link TenantRolePermissionBusinessService#tenantRolePermissionService}
     * @param tenantRolePermissionService instance of Tenant Role Permission Service to be set
     */
    public void setTenantRolePermissionService(TenantRolePermissionServiceAccess tenantRolePermissionService) {
        this.tenantRolePermissionService = tenantRolePermissionService;
    }
}
