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

import io.radien.api.model.tenantrole.SystemTenantRolePermission;
import io.radien.api.model.tenantrole.SystemTenantRolePermissionSearchFilter;
import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRolePermissionNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.client.entities.TenantRolePermissionSearchFilter;
import io.radien.ms.rolemanagement.client.services.TenantRolePermissionResourceClient;
import java.util.List;
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
@Authenticated
public class TenantRolePermissionResource extends AuthorizationChecker implements TenantRolePermissionResourceClient {

    private static final Logger log = LoggerFactory.getLogger(TenantRolePermissionResource.class);

    @Inject
    private TenantRolePermissionBusinessService tenantRolePermissionBusinessService;

    @Inject
    private TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;

    /**
     * Retrieves TenantRolePermission association using pagination approach
     * (in other words, retrieves the Permissions associations that exist for a TenantRole)
     * @param tenantRoleId identifier for a TenantRole (Optional)
     * @param permissionId identifier for a permission (Optional)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRolePermission associations (Chunk/Portion compatible
     * with parameter Page number and Page size).
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(Long tenantRoleId, Long permissionId, int pageNo, int pageSize,
                           List<String> sortBy,  boolean isAscending) {
        log.info("Retrieving TenantRole Permission associations using pagination. Page number {}. Page Size {}.",
                pageNo, pageSize);
        try {
            return Response.ok().entity(this.tenantRolePermissionServiceAccess.
                    getAll(tenantRoleId, permissionId, pageNo, pageSize, sortBy, isAscending)).build();
        }
        catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieves TenantRole Permission associations that met the following parameter
     * @param tenantRoleId TenantRole identifier
     * @param permissionId Permission identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns OK (http status 200)
     * and a Collection containing TenantRole associations.<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    public Response getSpecific(Long tenantRoleId, Long permissionId,boolean isLogicalConjunction) {
        log.info("Retrieving TenantRole Permission associations for tenantRole {} permission {} using logical function {}",
                tenantRoleId, permissionId, isLogicalConjunction);
        try {
            SystemTenantRolePermissionSearchFilter filter = new TenantRolePermissionSearchFilter(tenantRoleId,
                    permissionId, true, isLogicalConjunction);
            return Response.ok(tenantRolePermissionServiceAccess.get(filter)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieves a Tenant Role Permission using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @Override
    public Response getById(Long id) {
        try {
            log.info("Retrieving TenantRolePermission for id {}", id);
            SystemTenantRolePermission trp = tenantRolePermissionServiceAccess.get(id);
            if (trp == null) {
                return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
            }
            return Response.ok().entity(trp).build();
        } catch (Exception e) {
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
            if (!isCreateAllowed()) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
            tenantRolePermissionBusinessService.assignPermission(new io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity(tenantRolePermission));
            return Response.ok().build();
        } catch (TenantRoleException | UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Assign/associate/add permission to a TenantRole domain
     * The association will always be under a specific role
     * @param id corresponds to the identifier of the TenantRolePermission to be updated
     * @param tenantRolePermission instance containing the information to be updated
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    public Response update(long id, TenantRolePermission tenantRolePermission) {
        try {
            log.info("Updating TenantRolePermission id {} tenantRoleId {} permissionId {}", id,
                    tenantRolePermission.getTenantRoleId(), tenantRolePermission.getPermissionId());
            if (!isUpdateAllowed()) {
                return GenericErrorMessagesToResponseMapper.getForbiddenResponse();
            }
            tenantRolePermission.setId(id);
            tenantRolePermissionBusinessService.update(new io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity(tenantRolePermission));
            return Response.ok().build();
        } catch (TenantRolePermissionNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (TenantRoleException | UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    private boolean isCreateAllowed() throws SystemException {
        return  hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName()) || hasPermission(null,
                SystemActionsEnum.ACTION_CREATE.getActionName(), SystemResourcesEnum.TENANT_ROLE_PERMISSION.getResourceName()) ;
    }

    private boolean isUpdateAllowed() throws SystemException {
        return  hasGrant(SystemRolesEnum.SYSTEM_ADMINISTRATOR.getRoleName()) || hasPermission(null,
                SystemActionsEnum.ACTION_UPDATE.getActionName(), SystemResourcesEnum.TENANT_ROLE_PERMISSION.getResourceName()) ;
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
