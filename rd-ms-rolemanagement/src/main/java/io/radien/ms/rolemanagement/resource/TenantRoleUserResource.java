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

import io.radien.api.model.tenantrole.SystemTenantRoleUserSearchFilter;
import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUserSearchFilter;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserResourceClient;
import io.radien.ms.rolemanagement.service.TenantRoleUserBusinessService;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Resource implementation responsible for deal with operations
 * regarding Tenant Role User associations domain object
 * @author Newton Carvalho
 */
@RequestScoped
@Authenticated
public class TenantRoleUserResource extends AuthorizationChecker implements TenantRoleUserResourceClient {

    private static final Logger log = LoggerFactory.getLogger(TenantRoleUserResource.class);
    private static final long serialVersionUID = -107402438846356377L;

    @Inject
    private TenantRoleUserBusinessService tenantRoleUserBusinessService;

    /**
     * Retrieves a Tenant Role User using the id as search parameter.
     * @param id Tenant Role User id to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @Override
    public Response getById(Long id) {
        log.info("Retrieving TenantRoleUser for id {}", id);
        return Response.ok().entity(tenantRoleUserBusinessService.get(id)).build();
    }

    /**
     * Retrieves TenantRoleUser association using pagination approach
     * (in other words, retrieves the Users associations that exist for a TenantRole)
     * @param tenantRoleId identifier for a TenantRole (Optional)
     * @param userId identifier for a user (Optional)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRoleUser associations (Chunk/Portion compatible
     * with parameter Page number and Page size).
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(Long tenantRoleId, Long userId, int pageNo, int pageSize,
                           List<String> sortBy, boolean isAscending) {
        log.info("Retrieving TenantRole User associations using pagination. Page number {}. Page Size {}.",
                pageNo, pageSize);
        return Response.ok().entity(tenantRoleUserBusinessService.getAll(tenantRoleId, userId, pageNo, pageSize, sortBy, isAscending)).build();
    }

    /**
     * Retrieves TenantRoleUser association (Ids) using pagination approach
     * (in other words, retrieves the Users associations that exist for a TenantRole)
     * @param tenantId tenant identifier for a TenantRole
     * @param roleId role identifier for a TenantRole
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations Ids (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAllUserIds(Long tenantId, Long roleId, int pageNo, int pageSize) {
        log.info("Retrieving tenant role users ids. tenant id {} role id {}, pageNumber {} and pageSize {}",
                tenantId, roleId, pageNo, pageSize);
        return Response.ok().entity(tenantRoleUserBusinessService.getAllUserIds(tenantId, roleId, pageNo, pageSize)).build();
    }

    /**
     * Retrieves TenantRole User associations that met the following parameter
     * @param tenantRoleId TenantRole identifier
     * @param userId User identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns OK (http status 200)
     * and a Collection containing TenantRole associations.<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getSpecific(Long tenantRoleId, Long userId, boolean isLogicalConjunction) {
        log.info("Retrieving TenantRole User associations for tenantRole {} user {} using logical function {}",
                tenantRoleId, userId, isLogicalConjunction);
        SystemTenantRoleUserSearchFilter filter = new TenantRoleUserSearchFilter(tenantRoleId, userId, true, isLogicalConjunction);
        return Response.ok(tenantRoleUserBusinessService.getFiltered(filter)).build();
    }

    /**
     * Deletes a Tenant Role User association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 400 if tenant role user association could not be found ,
     * 500 code message if there is any error.
     */
    public Response delete(long id) {
        log.info("Deleting TenantRole User association for id {}", id);
        tenantRoleUserBusinessService.delete(id);
        return Response.ok().build();
    }

    /**
     * Assign/associate/add user to a TenantRole domain
     * The association will always be under a specific role
     * @param tenantRoleUser represents the association between Tenant, Role and User
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response assignUser(TenantRoleUser tenantRoleUser) {
        log.info("Associating/adding user {} to tenant-role {}", tenantRoleUser.getTenantRoleId(), tenantRoleUser.getUserId());
        try {
            if (!isCreateAllowed()) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
        } catch(SystemException e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
        tenantRoleUserBusinessService.assignUser(tenantRoleUser);
        return Response.ok().build();
    }

    /**
     * Updates a TenantRoleUser
     * @param id corresponds to the identifier of the TenantRoleUser to be updated
     * @param tenantRoleUser instance containing the information to be updated
     * @return Response OK if operation concludes with success.
     * Response status 404 in case of not existing a TenantRoleUser for the informed id,
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response update(long id, TenantRoleUser tenantRoleUser) {
        log.info("Updating TenantRoleUser id {} tenantRoleId {} userId {}", id, tenantRoleUser.getTenantRoleId(), tenantRoleUser.getUserId());
        try {
            if (!isUpdateAllowed()) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
        } catch (SystemException e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
        tenantRoleUserBusinessService.update(id, tenantRoleUser);
        return Response.ok().build();
    }

    protected boolean isCreateAllowed() throws SystemException {
        return tenantRoleUserBusinessService.getCount() == 0L ||
                hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName()) ||
                hasPermission(null, SystemActionsEnum.ACTION_CREATE.getActionName(), SystemResourcesEnum.TENANT_ROLE_USER.getResourceName());
    }

    protected boolean isUpdateAllowed() throws SystemException {
        return hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName()) ||
                hasPermission(null, SystemActionsEnum.ACTION_UPDATE.getActionName(), SystemResourcesEnum.TENANT_ROLE_USER.getResourceName());
    }

    /**
     * (Un)Assign/Dissociate/remove user from TenantRole domain
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleIds Roles identifiers
     * @param userId User identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response unAssignUser(Long tenantId, Collection<Long> roleIds, Long userId) {
        log.info("Dissociating/removing user {} from tenant {} roles {}", userId, tenantId, roleIds);
        tenantRoleUserBusinessService.unAssignUser(tenantId, roleIds, userId);
        return Response.ok().build();
    }

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return Response OK with List containing tenants. Response 500 in case of any other error.
     */
    @Override
    public Response getTenants(Long userId, Long roleId) {
        log.info("Retrieving tenants for user {} and role {}", userId, roleId);
        return Response.ok().entity(tenantRoleUserBusinessService.getTenants(userId, roleId)).build();
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
        return Response.ok().entity(tenantRoleUserBusinessService.getRolesForUserTenant(userId, tenantId)).build();
    }
}
