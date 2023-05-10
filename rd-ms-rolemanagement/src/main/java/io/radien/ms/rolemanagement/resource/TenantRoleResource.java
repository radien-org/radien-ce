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
package io.radien.ms.rolemanagement.resource;

import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.services.TenantRoleResourceClient;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.service.TenantRoleBusinessService;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource implementation responsible for deal with operations
 * regarding Tenant Role associations domain object
 * @author Newton Carvalho
 */
@RequestScoped
@Authenticated
public class TenantRoleResource extends AuthorizationChecker implements TenantRoleResourceClient {

    private static final Logger log = LoggerFactory.getLogger(TenantRoleResource.class);

    @Inject
    private TenantRoleBusinessService tenantRoleBusinessService;

    /**
     * Retrieve the association Id ({@link SystemTenantRole#getId()}) using the combination of tenant
     * and role as parameters
     * @param tenant tenant identifier
     * @param role role identifier
     * @return Response OK (200) with the retrieved id (if exists). If not exist will return 404 status.
     * In case of insufficient params (tenant or role not informed) It will return 400 status.
     * For any other kind of (unpredictable) error this endpoint will return status 500
     */
    @Override
    public Response getIdByTenantRole(Long tenant, Long role) {
        return Response.ok(tenantRoleBusinessService.getIdByTenantRole(tenant, role)).build();
    }

    /**
     * Retrieves TenantRole association using pagination approach
     * @param tenantId tenant identifier (Optional)
     * @param roleId role identifier (Optional)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(Long tenantId, Long roleId, int pageNo, int pageSize,
                           List<String> sortBy, boolean isAscending) {
        log.info("Retrieving TenantRole associations using pagination. Page number {}. Page Size {}.",
                pageNo, pageSize);
        return Response.ok().entity(tenantRoleBusinessService.getAll(tenantId, roleId, pageNo, pageSize, sortBy, isAscending)).build();
    }

    /**
     * Retrieves TenantRole associations that met the following parameter
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns OK (http status 200)
     * and a Collection containing TenantRole associations.<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getSpecific(Long tenantId, Long roleId, boolean isLogicalConjunction) {
        log.info("Retrieving TenantRole associations for tenant {} role {} using logical function {}",
                tenantId, roleId, isLogicalConjunction);
        return Response.ok(tenantRoleBusinessService.getFiltered(tenantId, roleId, isLogicalConjunction)).build();
    }

    /**
     * Retrieves a Tenant Role association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @Override
    public Response getById(Long id) {
        log.info("Retrieving TenantRole association for id {}", id);
        return Response.ok().entity(tenantRoleBusinessService.getById(id)).build();
    }

    /**
     * Deletes a Tenant Role association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 400 if association could not be found or if the association is attached to other entities
     * (i.e TenantRoleUser, TenantRolePermission, etc), 500 code message if there is any error.
     */
    @Override
    public Response delete(long id) {
        log.info("Deleting TenantRole association for id {}", id);
        tenantRoleBusinessService.delete(id);
        return Response.ok().build();
    }

    /**
     * Create a TenantRole association
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return 200 code message if success, 400 in case of duplication (association already
     * existing with the same parameter) or absence of information (tenant or role not existing),
     * 500 code message if there is any error.
     */
    @Override
    public Response create(TenantRole tenantRole) {
        log.info("Creating association for Tenant {} and Role {}", tenantRole.getTenantId(), tenantRole.getRoleId());
        try {
            if (!isCreateAllowed()) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
        } catch (SystemException ex) {
            return GenericErrorMessagesToResponseMapper.getGenericError(ex);
        }

        tenantRoleBusinessService.create(new TenantRoleEntity(tenantRole));
            return Response.ok().build();
    }

    /**
     * Update a TenantRole association
     * @param id id related to the TenantRole that is going to be updated
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return 200 code message if success, 400 in case of duplication (association already
     * existing with the same parameter) or absence of information (tenant or role not existing),
     * 404 if the there is not TenantRole for the informed Id,
     * 500 code message if there is any error.
     */
    public Response update(Long id, TenantRole tenantRole) {
        log.info("Updating association for Tenant {} and Role {}", tenantRole.getTenantId(), tenantRole.getRoleId());
        try {
            if (!isUpdateAllowed()) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
        tenantRoleBusinessService.update(id, tenantRole);
        return Response.ok().build();
    }

    private boolean isCreateAllowed() throws SystemException {
        return tenantRoleBusinessService.count() == 0L ||
                hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName())
                || hasPermission(null, SystemActionsEnum.ACTION_CREATE.getActionName(), SystemResourcesEnum.TENANT_ROLE.getResourceName());
    }


    private boolean isUpdateAllowed() throws SystemException {
        return hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName())
                || hasPermission(null, SystemActionsEnum.ACTION_UPDATE.getActionName(), SystemResourcesEnum.TENANT_ROLE.getResourceName());
    }

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return Responds with 204 http status if association exists, 404 (NOT FOUND) otherwise.
     * Response 500 in case of any other error.
     */
    @Override
    public Response exists(Long tenantId, Long roleId) {
        log.info("Checking if Tenant Role association exists for tenant {} and role {}", tenantId, roleId);
        if (tenantRoleBusinessService.existsAssociation(tenantId, roleId)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }


    /**
     * Check if Role exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param roleName Role name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if role is associated to an User, otherwise false).
     * Response 404 in case of absence of parameter like user identifier or role name.
     * Response 500 in case of any error
     */
    @Override
    public Response isRoleExistentForUser(Long userId, String roleName, Long tenantId) {
        log.info("Checking if role {} exists for user {} under tenant {}", roleName, userId, tenantId);
        if (roleName == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().entity(tenantRoleBusinessService.isRoleExistentForUser(userId, roleName, tenantId)).build();
    }

    /**
     * Check if some Role exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param roleNames Role names identifiers
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if there is some role is associated to the User,
     * otherwise false).
     * Response 404 in case of absence of parameter like user identifier or role name.
     * Response 500 in case of any error
     */
    @Override
    public Response isAnyRoleExistentForUser(Long userId, List<String> roleNames, Long tenantId) {
        log.info("Checking if user {} has roles for tenantId {}", userId, tenantId);
        return Response.ok().entity(tenantRoleBusinessService.isAnyRoleExistentForUser(userId, roleNames, tenantId)).build();
    }

    /**
     * Check if Permission exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if permission is associated to an User, otherwise false).
     * Response 404 in case of absence of parameter like user identifier or permission identifier.
     * Response 500 in case of any error
     */
    @Override
    public Response isPermissionExistentForUser(Long userId, Long permissionId, Long tenantId) {
        log.info("Checking if permission {} exists for user {} under tenant {}", permissionId, userId, tenantId);
        return Response.ok().entity(tenantRoleBusinessService.isPermissionExistentForUser(userId, permissionId, tenantId)).build();
    }

    /**
     * Retrieves the Roles for which a User is associated under a Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return Response OK with List containing roles. Response 500 in case of any other error.
     */
    @Override
    public Response getRolesForUserTenant(Long userId, Long tenantId) {
        log.info("Retrieving Roles for user {} and tenant {}", userId, tenantId);
        return Response.ok().entity(tenantRoleBusinessService.getRolesForUserTenant(userId, tenantId)).build();
    }
}
