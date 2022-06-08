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

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InvalidArgumentException;
import io.radien.api.service.role.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;

import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_ASSOCIATION_TENANT_ROLE;

/**
 * Component that orchestrates the using of diverse Service Access components
 * to handle TenantRole domain business rules
 * @author Newton Carvalho
 */
@Stateless
public class TenantRoleBusinessService {

    protected Logger log = LoggerFactory.getLogger(TenantRoleBusinessService.class);
    @Inject
    private RoleServiceAccess roleServiceAccess;
    @Inject
    private TenantRoleServiceAccess tenantRoleService;

    /**
     * Retrieves TenantRole association using pagination approach
     * @param pageNumber page number
     * @param pageSize page size
     * @return Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     */
    public Page<SystemTenantRole> getAll(Long tenantId, Long roleId, int pageNumber, int pageSize,
                                         List<String> sortBy, boolean isAscending) {
        return tenantRoleService.getAll(tenantId, roleId, pageNumber, pageSize, sortBy, isAscending);
    }

    /**
     * Retrieves TenantRole associations that met the following parameter
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return Collection containing TenantRole associations.
     */
    public List<? extends SystemTenantRole> getFiltered(Long tenantId, Long roleId, boolean isLogicalConjunction) {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(tenantId, roleId, true, isLogicalConjunction);
        return tenantRoleService.get(filter);
    }

    /**
     * Gets the System Tenant Role association searched by the PK (id).
     * @param id to be searched.
     * @return the system Tenant Role Association requested to be found.
     * @throws TenantRoleNotFoundException if Tenant Role association could not be found
     * @throws BadRequestException if there are missing parameters
     */
    public SystemTenantRole getById(Long id) {
        SystemTenantRole systemTenantRole = tenantRoleService.get(id);
        if (systemTenantRole == null) {
            throw new TenantRoleNotFoundException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(id.toString()));
        }
        return systemTenantRole;
    }

    public Long getIdByTenantRole(Long tenantId, Long roleId) {
        try {
            return tenantRoleService.getTenantRoleId(tenantId, roleId)
                    .orElseThrow(() ->  new TenantRoleNotFoundException(
                            TENANT_ROLE_ASSOCIATION_TENANT_ROLE.toString(tenantId.toString(), roleId.toString()))
                    );
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Deletes a Tenant Role association
     * @param id Tenant Role association Identifier
     * @throws TenantRoleNotFoundException if no tenant role for id is found
     * @throws BadRequestException if it is not possible to delete the passed tenant role
     */
    public void delete(Long id) {
        try {
            if(!tenantRoleService.delete(id)) {
                throw new TenantRoleNotFoundException(MessageFormat.format("No tenant role found with id {0}", id));
            }
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Create a TenantRole association
     * @param systemTenantRole bean that corresponds to TenantRole association
     * @throws BadRequestException if object does not meet all criteria
     * @throws RoleException if duplicated information is present
     */
    public void create(SystemTenantRole systemTenantRole) {
        try {
            tenantRoleService.create(systemTenantRole);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (UniquenessConstraintException e) {
            throw new RoleException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Create a TenantRole association
     * @param tenantRole bean that corresponds to TenantRole association
     * @throws BadRequestException if object does not meet all criteria
     * @throws RoleException if duplicated information is present
     */
    public void update(Long id, TenantRole tenantRole)  {
        try {
            tenantRole.setId(id);
            tenantRoleService.update(new TenantRoleEntity(tenantRole));
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (UniquenessConstraintException e) {
            throw new RoleException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true if exists, otherwise false
     */
    public boolean existsAssociation(Long tenantId, Long roleId) {
        try {
            return tenantRoleService.isAssociationAlreadyExistent(roleId, tenantId);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Check if Role exists for a User (Optionally for a Tenant)
     * @param userId User identifier
     * @param roleName Role name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if role is associated to an User, otherwise false
     * @throws BadRequestException if there are missing arguments
     */
    public boolean isRoleExistentForUser(Long userId, String roleName, Long tenantId) {
        try {
            return tenantRoleService.hasAnyRole(userId, Collections.singletonList(roleName), tenantId);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Check if some of the specifies Role exists for a User (Optionally for a Tenant)
     * @param userId User identifier
     * @param roleNames Roles name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if role is associated to an User, otherwise false
     */
    public boolean isAnyRoleExistentForUser(Long userId, List<String> roleNames, Long tenantId) {
        if (roleNames == null || roleNames.isEmpty()) {
            throw new BadRequestException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("role names"));
        }
        try {
            return tenantRoleService.hasAnyRole(userId, roleNames, tenantId);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Check if Permission exists for a User (Optionally for a Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if role is associated to an User, otherwise false
     * @throws BadRequestException if mandatory arguments are missing
     */
    public boolean isPermissionExistentForUser(Long userId, Long permissionId, Long tenantId) {
        try {
            return tenantRoleService.hasPermission(userId, permissionId, tenantId);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public long count() {
        return tenantRoleService.count();
    }

    /**
     * Retrieves the existent Roles for a User of a specific Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing roles
     * @throws BadRequestException if mandatory arguments are missing
     */
    public List<? extends SystemRole> getRolesForUserTenant(Long userId, Long tenantId) {
        try {
            log.info("Get Roles for User {} Tenant {}", userId, tenantId);
            List<Long> ids = tenantRoleService.getRoleIdsForUserTenant(userId, tenantId);
            if(ids == null || ids.isEmpty()){
                return new ArrayList<>();
            }
            return roleServiceAccess.getSpecificRoles(new RoleSearchFilter(null, null, ids, true,true));
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
