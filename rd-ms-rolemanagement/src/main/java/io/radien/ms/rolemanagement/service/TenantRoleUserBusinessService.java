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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.role.exception.RoleException;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.InvalidArgumentException;
import io.radien.exception.SystemException;
import io.radien.api.service.role.exception.TenantRoleNotFoundException;
import io.radien.api.service.role.exception.TenantRoleUserNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR_PARAMS;

/**
 * TenantRoleUserBusinessService bridge between REST Services and
 * Persistence layer of TenantRoleUserService
 *
 * @author Rajesh Gavvala
 */
@Stateless
public class TenantRoleUserBusinessService implements Serializable {
    private static final long serialVersionUID = -5658845596283229172L;

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;
    @Inject
    private TenantRoleBusinessService tenantRoleService;
    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;
    @Inject
    private TenantRESTServiceAccess tenantRESTService;


    /**
     * Retrieves TenantRoleUser by given id
     * @param tenantRoleUserId object it to look for
     * @return found TenantRoleUser
     * @throws TenantRoleUserNotFoundException if object for given id is not found
     */
    public SystemTenantRoleUser get(Long tenantRoleUserId) {
        SystemTenantRoleUser result = tenantRoleUserServiceAccess.get(tenantRoleUserId);
        if(result == null) {
            throw new TenantRoleUserNotFoundException(MessageFormat.format("No tenant role user found for id {0}", tenantRoleUserId));
        }
        return result;
    }

    /**
     * Gets all the tenant role user associations into a pagination mode.
     * @param tenantRoleId search param that corresponds to the TenantRole id (Optional)
     * @param userId search param that corresponds to the user id (Optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return a page containing system tenant role user associations.
     */
    public Page<SystemTenantRoleUser> getAll(Long tenantRoleId, Long userId, int pageNo, int pageSize,
                                             List<String> sortBy, boolean isAscending) {
        return tenantRoleUserServiceAccess.getAll(tenantRoleId, userId, pageNo, pageSize, sortBy, isAscending);
    }

    /**
     * Gets all the tenant role user associations into a pagination mode.
     * @param tenant search param that corresponds to the TenantRole.tenantId (Optional)
     * @param role search param that corresponds to the TenantRole.roleId (Optional)
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @return a page containing system tenant role user associations.
     */
    public Page<Long> getAllUserIds(Long tenant, Long role, int pageNo, int pageSize) {
        return tenantRoleUserServiceAccess.getAllUserIds(tenant, role, pageNo, pageSize);
    }

    /**
     * Gets all the tenants role user associations matching the given filter information
     * @param filter information to search
     * @return a list o found system tenant role user associations
     */
    public List<? extends SystemTenantRoleUser> getFiltered(SystemTenantRoleUserSearchFilter filter) {
        return tenantRoleUserServiceAccess.get(filter);
    }

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tru TenantRoleUser bean that contains information regarding user and Tenant role association
     * @throws BadRequestException if arguments are invalid or association already exists
     * @throws RoleException in case of duplicate data
     */
    public void assignUser(TenantRoleUser tru) {
        SystemTenantRole tenantRole = tenantRoleService.getById(tru.getTenantRoleId());
        try {
            if (tenantRoleUserServiceAccess.isAssociationAlreadyExistent(tru.getUserId(), tru.getTenantRoleId(), tru.getId())) {
                throw new BadRequestException(
                        GenericErrorCodeMessage.TENANT_ROLE_USER_IS_ALREADY_ASSOCIATED.toString(
                                String.valueOf(tenantRole.getTenantId()),
                                String.valueOf(tenantRole.getRoleId()))
                );
            }
            this.tenantRoleUserServiceAccess.create(new TenantRoleUserEntity(tru));
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (UniquenessConstraintException e) {
            throw new RoleException(e.getMessage(), Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tru TenantRoleUser bean that contains information regarding user and Tenant role association
     * @throws BadRequestException if arguments are invalid or association already exists
     * @throws RoleException in case of duplicate data
     */
    public void update(Long id, TenantRoleUser tru) {
        tru.setId(id);
        SystemTenantRole tenantRole = tenantRoleService.getById(tru.getTenantRoleId());
        try {
            if (tenantRoleUserServiceAccess.isAssociationAlreadyExistent(tru.getUserId(), tru.getTenantRoleId(), tru.getId())) {
                throw new BadRequestException(
                        GenericErrorCodeMessage.TENANT_ROLE_USER_IS_ALREADY_ASSOCIATED.toString(
                                String.valueOf(tenantRole.getTenantId()),
                                String.valueOf(tenantRole.getRoleId()))
                );
            }
            this.tenantRoleUserServiceAccess.update(new TenantRoleUserEntity(tru));
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (UniquenessConstraintException e) {
            throw new RoleException(e.getMessage(), Response.Status.BAD_REQUEST);
        }    }

    /**
     * Deletes a Tenant Role Permission association
     * @param id Tenant Role Id association Identifier
     * @throws TenantRoleUserNotFoundException if given id for tenant role user does not exist
     * @throws TenantRoleNotFoundException if given tenant role does not exist
     * @throws RoleException if it was not possible to delete active tenant
     */
    public void delete(Long id) {
        SystemTenantRoleUser systemTenantRoleUser = get(id);
        SystemTenantRole tenantRole = tenantRoleService.getById(systemTenantRoleUser.getTenantRoleId());
        if(tenantRoleUserServiceAccess.delete(id)) {
            deleteActiveTenant(systemTenantRoleUser.getUserId(), tenantRole.getTenantId());
        } else {
            throw new TenantRoleUserNotFoundException(MessageFormat.format("No tenant role user found for id {0}", id));
        }
    }

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param roles Roles identifiers
     * @param user User identifier (Always Mandatory)
     * @throws TenantRoleUserNotFoundException if given tenantRoleUser is not found
     * @throws BadRequestException if any mandatory parameters are missing
     * @throws RoleException if it was not possible to delete active tenant
     */
    public void unAssignUser(Long tenant, Collection<Long> roles, Long user) {
        try {
            Collection<Long> ids = tenantRoleUserServiceAccess.getTenantRoleUserIds(tenant, roles, user);
            if (ids.isEmpty()) {
                throw new TenantRoleUserNotFoundException(
                        TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR_PARAMS.toString(String.valueOf(tenant), String.valueOf(roles), String.valueOf(user))
                );
            }
            tenantRoleUserServiceAccess.delete(ids);
            deleteActiveTenant(user, tenant);
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenants
     * @throws BadRequestException if mandatory arguments are missing
     * @throws RoleException if it was not possible to delete active tenant
     */
    public List<SystemTenant> getTenants(Long userId, Long roleId) {
        try {
            List<SystemTenant> list = new ArrayList<>();
            List<Long> ids = tenantRoleUserServiceAccess.getTenants(userId, roleId);
            if (!ids.isEmpty()) {
                list.addAll(tenantRESTService.getTenantsByIds(ids));
            }
            return list;
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (SystemException e) {
            throw new RoleException(e.getMessage());
        }
    }

    /**
     * After remove/delete/dissociate a user (TenantRoleUser) is necessary to handle
     * the Active Tenant (remove them as well)
     * @param user user identifier
     * @param tenant tenant identifier
     * @throws BadRequestException if mandatory arguments are missing
     * @throws RoleException if it was not possible to delete active tenant
     */
    protected void deleteActiveTenant(Long user, Long tenant) {
        try {
            if (!tenantRoleUserServiceAccess.isAssociatedWithTenant(user, tenant)) {
                activeTenantRESTServiceAccess.deleteByTenantAndUser(tenant, user);
            }
        } catch (InvalidArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (SystemException e) {
            throw new RoleException(e.getMessage());
        }
    }

    /**
     * Retrieves the existent Roles for a User of a specific Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing roles
     * @throws BadRequestException if mandatory arguments are missing
     */
    public List<? extends SystemRole> getRolesForUserTenant(Long userId, Long tenantId) {
        return tenantRoleService.getRolesForUserTenant(userId, tenantId);
    }

    public long getCount() {
        return tenantRoleUserServiceAccess.count();
    }

}
