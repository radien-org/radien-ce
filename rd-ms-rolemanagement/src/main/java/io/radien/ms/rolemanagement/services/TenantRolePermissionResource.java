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

import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.services.TenantRolePermissionResourceClient;
import javax.ejb.EJBException;
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
public class TenantRolePermissionResource implements TenantRolePermissionResourceClient {

    private static final Logger log = LoggerFactory.getLogger(TenantRolePermissionResource.class);

    @Inject
    private TenantRolePermissionBusinessService tenantRolePermissionBusinessService;

    /**
     * Retrieves TenantRolePermission association using pagination approach
     * (in other words, retrieves the Permissions associations that exist for a TenantRole)
     * @param tenantId tenant identifier for a TenantRole
     * @param roleId role identifier for a TenantRole
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRolePermission associations (Chunk/Portion compatible
     * with parameter Page number and Page size).
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(Long tenantId, Long roleId, int pageNo, int pageSize) {
        log.info("Retrieving TenantRole Permission associations using pagination. Page number {}. Page Size {}.",
                pageNo, pageSize);
        try {
            return Response.ok().entity(this.tenantRolePermissionBusinessService.
                    getAll(tenantId, roleId, pageNo, pageSize)).build();
        }
        catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Deletes a Tenant Role Permission association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 400 if tenant role permission association could not be found ,
     * 500 code message if there is any error.
     */
    public Response delete(long id) {
        try {
            log.info("Deleting TenantRole Permission association for id {}", id);
            return Response.ok().entity(tenantRolePermissionBusinessService.delete(id)).build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Assign/associate/add permission to a TenantRole domain
     * The association will always be under a specific role
     * @param tenantRolePermission represents the association between Tenant, Role and Permission
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response assignPermission(TenantRolePermission tenantRolePermission) {
        try {
            log.info("Associating/adding permission {} to tenant-role {}", tenantRolePermission.getTenantRoleId(),
                    tenantRolePermission.getPermissionId());
            tenantRolePermissionBusinessService.assignPermission(new io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity(tenantRolePermission));
            return Response.ok().build();
        } catch (TenantRoleException | UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove permission from TenantRole domain
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response unAssignPermission(Long tenantId, Long roleId, Long permissionId) {
        try {
            log.info("Dissociating/removing permission {} from tenant {} role {}", permissionId, tenantId, roleId);
            tenantRolePermissionBusinessService.unAssignPermission(tenantId, roleId, permissionId);
            return Response.ok().build();
        } catch (TenantRoleException | EJBException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }


    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return Response OK with List containing permissions. Response 500 in case of any other error.
     */
    @Override
    public Response getPermissions(Long tenantId, Long roleId, Long userId) {
        log.info("Retrieving permissions for tenant {} role {} and user {}", tenantId, roleId, userId);
        try {
            return Response.ok().entity(tenantRolePermissionBusinessService.
                    getPermissions(tenantId, roleId, userId)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

}
