/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.services.TenantRoleResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Resource implementation responsible for deal with operations
 * regarding Tenant Role associations domain object
 * @author Newton Carvalho
 */
@RequestScoped
public class TenantRoleResource implements TenantRoleResourceClient {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private TenantRoleBusinessService tenantRoleBusinessService;

    /**
     * Retrieves TenantRole association using pagination approach
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(int pageNo, int pageSize) {
        log("Retrieving TenantRole associations using pagination. Page number %d. Page Size %d.",
                pageNo, pageSize);
        try {
            return Response.ok().entity(this.tenantRoleBusinessService.getAll(pageNo, pageSize)).build();
        }
        catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
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
        log("Retrieving TenantRole associations for tenant %d role %d using logical function %b",
                tenantId, roleId, isLogicalConjunction);
        try {
            return Response.ok(tenantRoleBusinessService.getSpecific(tenantId, roleId, isLogicalConjunction)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieves a Tenant Role association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @Override
    public Response getById(Long id) {
        try {
            log("Retrieving TenantRole association for id %d", id);
            return Response.ok().entity(tenantRoleBusinessService.getById(id)).build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
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
        try {
            log("Deleting TenantRole association for id %d", id);
            return Response.ok().entity(tenantRoleBusinessService.delete(id)).build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Create a TenantRole association
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return 200 code message if success, 400 in case of duplication (association already
     * existing with the same parameter) or absence of information (tenant or role not existing),
     * 500 code message if there is any error.
     */
    @Override
    public Response save(TenantRole tenantRole) {
        try {
            log("Creating association for Tenant %d and Role %d", tenantRole.getTenantId(), tenantRole.getRoleId());
            tenantRoleBusinessService.save(new io.radien.ms.rolemanagement.entities.TenantRole(tenantRole));
            return Response.ok().build();
        } catch (UniquenessConstraintException | TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return Response OK containing true (if association exists), false otherwise.
     * Response 500 in case of any other error.
     */
    @Override
    public Response exists(Long tenantId, Long roleId) {
        log("Checking if Tenant Role association exists for tenant %d and role %d", tenantId, roleId);
        try {
            return Response.ok().entity(tenantRoleBusinessService.existsAssociation(tenantId, roleId)).build();
        } catch(Exception e) {
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
        log("Retrieving permissions for tenant %d role %d and user %d", tenantId, roleId, userId);
        try {
            return Response.ok().entity(tenantRoleBusinessService.
                    getPermissions(tenantId, roleId, userId)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return Response OK with List containing tenants. Response 500 in case of any other error.
     */
    @Override
    public Response getTenants(Long userId, Long roleId) {
        log("Retrieving tenants for user %d and role %d", userId, roleId);
        try {
            return Response.ok().entity(tenantRoleBusinessService.
                    getTenants(userId, roleId)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieves the Roles for which a User is associated under a Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return Response OK with List containing roles. Response 500 in case of any other error.
     */
    @Override
    public Response getRolesForUserTenant(Long userId, Long tenantId) {
        log(GenericErrorCodeMessage.INFO_TENANT_USER.toString(String.valueOf(userId), String.valueOf(tenantId)));
        try {
            return Response.ok().entity(tenantRoleBusinessService.getRolesForUserTenant(userId, tenantId)).build();
        } catch (RoleNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        }
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
        log("Checking if role %s exists for user %d under tenant %d", roleName, userId, tenantId);
        if (userId == null || roleName == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok().entity(tenantRoleBusinessService.
                    isRoleExistentForUser(userId, roleName, tenantId)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
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
        log("Checking if user %d has roles for tenantId %d", userId, tenantId);
        if (userId == null || roleNames == null || roleNames.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok().entity(tenantRoleBusinessService.
                    isAnyRoleExistentForUser(userId, roleNames, tenantId)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
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
        log("Checking if permission %d exists for user %d under tenant %d", permissionId, userId, tenantId);
        if (userId == null || permissionId == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        try {
            return Response.ok().entity(tenantRoleBusinessService.
                    isPermissionExistentForUser(userId, permissionId, tenantId)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response assignUser(Long tenantId, Long roleId, Long userId) {
        try {
            log("Associating/adding user %d to tenant %d role %d", userId, tenantId, roleId);
            tenantRoleBusinessService.assignUser(tenantId, roleId, userId);
            return Response.ok().build();
        } catch (TenantRoleException | UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier
     * @param userId User identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response unassignUser(Long tenantId, Long roleId, Long userId) {
        try {
            log("Dissociating/removing user %d from tenant %d role %d", userId, tenantId, roleId);
            tenantRoleBusinessService.unassignUser(tenantId, roleId, userId);
            return Response.ok().build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response assignPermission(Long tenantId, Long roleId, Long permissionId) {
        try {
            log("Associating/adding permission %d to tenant %d role %d", permissionId, tenantId, roleId);
            tenantRoleBusinessService.assignPermission(tenantId, roleId, permissionId);
            return Response.ok().build();
        } catch (TenantRoleException | UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @Override
    public Response unassignPermission(Long tenantId, Long roleId, Long permissionId) {
        try {
            log("Dissociating/removing permission %d from tenant %d role %d", permissionId, tenantId, roleId);
            tenantRoleBusinessService.unassignPermission(tenantId, roleId, permissionId);
            return Response.ok().build();
        } catch (TenantRoleException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Utility method to log messages (in a cleaner approach)
     * @param msg message to be logged
     * @param params message params
     */
    private void log(String msg, Object... params) {
        if (msg != null) {
            String formattedMsg = params != null ? String.format(msg, params) : msg;
            log.error(formattedMsg);
        }
    }
}
