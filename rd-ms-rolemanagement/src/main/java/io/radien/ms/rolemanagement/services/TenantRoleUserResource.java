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

import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.services.TenantRoleUserResourceClient;
import java.util.Collection;
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
public class TenantRoleUserResource extends AuthorizationChecker implements TenantRoleUserResourceClient {

    private static final Logger log = LoggerFactory.getLogger(TenantRoleUserResource.class);

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    @Inject
    private TenantRoleUserBusinessService tenantRoleUserBusinessService;

    /**
     * Retrieves TenantRoleUser association using pagination approach
     * (in other words, retrieves the Users associations that exist for a TenantRole)
     * @param tenantId tenant identifier for a TenantRole
     * @param roleId role identifier for a TenantRole
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(Long tenantId, Long roleId, int pageNo, int pageSize) {
        log.info("Retrieving tenant role users. tenant id {} role id {}, pageNumber {} and pageSize {}",
                tenantId, roleId, pageNo, pageSize);
        try {
            return Response.ok().entity(tenantRoleUserServiceAccess.
                    getAll(tenantId, roleId, pageNo, pageSize)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
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
        try {
            return Response.ok().entity(tenantRoleUserServiceAccess.
                    getAllUserIds(tenantId, roleId, pageNo, pageSize)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Deletes a Tenant Role User association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 400 if tenant role user association could not be found ,
     * 500 code message if there is any error.
     */
    public Response delete(long id) {
        try {
            log.info("Deleting TenantRole User association for id {}", id);
            return Response.ok().entity(tenantRoleUserBusinessService.delete(id)).build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
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
        try {
            log.info("Associating/adding user {} to tenant-role {}", tenantRoleUser.getTenantRoleId(),
                    tenantRoleUser.getUserId());
            if (!isSaveAllowed(tenantRoleUser)) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
            tenantRoleUserBusinessService.assignUser(new io.radien.ms.rolemanagement.entities.TenantRoleUserEntity(tenantRoleUser));
            return Response.ok().build();
        } catch (TenantRoleException | UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    private boolean isSaveAllowed(TenantRoleUser tenantRoleUser) throws SystemException {


        return  tenantRoleUserServiceAccess.count()==0L || hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName()) || hasPermission(null,
                tenantRoleUser.getId()==null? SystemActionsEnum.ACTION_CREATE.getActionName():SystemActionsEnum.ACTION_UPDATE.getActionName(),
                SystemResourcesEnum.TENANT_ROLE_USER.getResourceName()) ;
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
        try {
            log.info("Dissociating/removing user {} from tenant {} roles {}", userId, tenantId, roleIds);
            tenantRoleUserBusinessService.unAssignUser(tenantId, roleIds, userId);
            return Response.ok().build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }
}
