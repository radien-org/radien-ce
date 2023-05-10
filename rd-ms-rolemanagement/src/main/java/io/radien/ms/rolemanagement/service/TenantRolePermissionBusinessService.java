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
package io.radien.ms.rolemanagement.service;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.role.exception.RoleNotFoundException;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.SystemException;
import io.radien.api.service.role.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import io.radien.ms.rolemanagement.util.ValidationUtil;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE;

/**
 * Component that orchestrates the using of diverse Service Access components
 * to handle TenantRole (Permission) domain business rules
 * @author Newton Carvalho
 */
@Stateless
public class TenantRolePermissionBusinessService implements Serializable {

    @Inject
    private TenantRolePermissionServiceAccess tenantRolePermissionService;
    @Inject
    private TenantRoleBusinessService tenantRoleService;
    @Inject
    private PermissionRESTServiceAccess permissionRESTService;



    /**
     * Gets all the role information into a paginated mode and return those information to the user.
     * In case of omitted (empty) search parameter retrieves ALL roles
     * @param tenantRoleId search parameter for matching roles (optional).
     * @param permissionid search parameter for matching roles (optional).
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     */
    public Page<? extends SystemTenantRolePermission> getAll(Long tenantRoleId, Long permissionid, int pageNo, int pageSize, List<String> sortBy,
                                             boolean isAscending) {
        return tenantRolePermissionService.getAll(tenantRoleId, permissionid, pageNo, pageSize, sortBy, isAscending);
    }

    /**
     * Retrieve all the information which has a specific name or description.
     * @return a paginated response with the requested information. 200 code message if success, 500 code message
     * if there is any error.
     */
    public List<? extends SystemTenantRolePermission> getFiltered(SystemTenantRolePermissionSearchFilter filter) {
        return tenantRolePermissionService.get(filter);
    }

    /**
     *  Gets the information of a tenant role permission which will be found using the id.
     *
     * @param id to be searched for
     * @throws RoleNotFoundException if the role for the requested id is not found
     * @return the requested TenantRolePermission
     */
    public SystemTenantRolePermission getById(Long id) {
        SystemTenantRolePermission result = tenantRolePermissionService.get(id);
        if(result == null) {
            throw new RoleNotFoundException(MessageFormat.format("No tenant role permission found for ID {0}", id));
        }
        return result;
    }

    /**
     * Deletes a Tenant Role Permission association
     * @param id Tenant Role Id association Identifier
     * @return If the association was found and the delete process was successfully performed
     * @throws RoleNotFoundException if no tenant role permission exists for the given id
     */
    public void delete(Long id) {
        if(!tenantRolePermissionService.delete(id)) {
            throw new RoleNotFoundException(MessageFormat.format("No tenant role permission found for ID {0}", id));
        }
    }

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantRolePermission Association among Tenant, Role and Permission (Mandatory)
     * @throws RoleException in case no actions were performed because of other factors
     * @throws BadRequestException if object does not have all necessary parameters
     * @throws TenantRoleNotFoundException if given tenant role does not exist
     */
    public void assignPermission(TenantRolePermissionEntity tenantRolePermission) {
        try {
            validateParameters(tenantRolePermission);
            if (tenantRolePermissionService.isAssociationAlreadyExistent(tenantRolePermission.getPermissionId(), tenantRolePermission.getTenantRoleId())) {
                SystemTenantRole tenantRole = tenantRoleService.getById(tenantRolePermission.getTenantRoleId());
                throw new RoleException(
                        TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE.toString(String.valueOf(tenantRole.getTenantId()), String.valueOf(tenantRole.getRoleId())),
                        Response.Status.ACCEPTED
                );
            }
            tenantRolePermissionService.create(tenantRolePermission);
        } catch (UniquenessConstraintException | InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Update a TenantRolePermission (In other words, updates a permission
     * assignment done previously)
     * @param tenantRolePermission Association among Tenant, Role and Permission (Mandatory)
     * @throws RoleException for the case of any inconsistency found
     * @throws BadRequestException in case of error during the insertion
     */
    public void update(TenantRolePermissionEntity tenantRolePermission) {
        validateParameters(tenantRolePermission);
        SystemTenantRole tenantRole = tenantRoleService.getById(tenantRolePermission.getTenantRoleId());

        if (tenantRolePermissionService.isAssociationAlreadyExistent(tenantRolePermission.getPermissionId(), tenantRolePermission.getTenantRoleId(),
                tenantRolePermission.getId())) {
            throw new RoleException(
                    TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE.toString(String.valueOf(tenantRole.getTenantId()), String.valueOf(tenantRole.getRoleId())),
                    Response.Status.ACCEPTED
            );
        }
        try {
            tenantRolePermissionService.update(tenantRolePermission);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (UniquenessConstraintException e) {
            throw new RoleException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param permission User identifier (Mandatory)
     * @throws BadRequestException for the case of any inconsistency found
     * @throws RoleException in case anything goes wrong
     */
    public void unAssignPermission(Long tenant, Long role, Long permission) {
        try {
            ValidationUtil.checkIfMandatoryParametersWereInformed(tenant, role, permission);
            Long tenantRoleId = tenantRoleService.getIdByTenantRole(tenant, role);
            Long tenantRolePermissionId = tenantRolePermissionService.getTenantRolePermissionId(tenantRoleId, permission)
                    .orElseThrow(() -> new RoleException(
                            TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION.toString(permission.toString()))
                    );
            tenantRolePermissionService.delete(tenantRolePermissionId);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions
     * @throws BadRequestException in case there are missing parameters or requests don't go through
     */
    public List<SystemPermission> getPermissions(Long tenantId, Long roleId, Long userId) {
        try {
            List<SystemPermission> list = new ArrayList<>();
            List<Long> ids = tenantRolePermissionService.getPermissions(tenantId, roleId, userId);
            if (!ids.isEmpty()) {
                list.addAll(permissionRESTService.getPermissionsByIds(ids));
            }
            return list;
        } catch (InvalidArgumentException | SystemException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Check if all the informed Params (Tenant, Role, etc) exist
     * @param tenantRolePermissionEntity Permission identifier
     * @throws RoleException in case of error during communication process using REST Clients
     * @throws BadRequestException in case of any inconsistency (ex: tenant not found for the informed id)
     */
    private void validateParameters(TenantRolePermissionEntity tenantRolePermissionEntity) {
        if(tenantRolePermissionEntity.getPermissionId() == null) {
            throw new BadRequestException(TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.PERMISSION_ID.getLabel()));
        }
        if(tenantRolePermissionEntity.getTenantRoleId() == null) {
            throw new BadRequestException(TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ROLE_ID.getLabel()));
        }
        try {
            if (!permissionRESTService.isPermissionExistent(tenantRolePermissionEntity.getPermissionId(), null)) {
                throw new BadRequestException(
                        GenericErrorCodeMessage.TENANT_ROLE_NO_PERMISSION_FOUND.toString(String.valueOf(tenantRolePermissionEntity))
                );
            }
        } catch(SystemException e) {
            throw new RoleException(e.getMessage());
        }
    }
}
