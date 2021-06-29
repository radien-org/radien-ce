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

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.LinkedAuthorizationException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorizationSearchFilter;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Linked Authorization resource that will request the service to validate the actions on
 * the db
 *
 * @author Bruno Gama
 */
@Path("linkedauthorization")
@RequestScoped
public class LinkedAuthorizationResource implements LinkedAuthorizationResourceClient {

    @Inject
    private LinkedAuthorizationBusinessService linkedAuthorizationBusinessService;

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationResource.class);

    /**
     * Gets all the role information into a paginated mode and return those information to the user.
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
     * error.
     */
    @Override
    public Response getAllAssociations(int pageNo, int pageSize) {
        try {
            log.info("Will get all the role information I can find!");
            return Response.ok(linkedAuthorizationBusinessService.getAll(pageNo, pageSize)).build();
        } catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieve all the information which has a specific tenant id, permission id, role id or user id
     * @param tenantId to be find.
     * @param permissionId to be find.
     * @param roleId to be find.
     * @param isLogicalConjunction true if the value to be searched must be exactly as it is given
     *                     or false if it must only contain such value.
     * @return a paginated response with the requested information. 200 code message if success, 500 code message
     *          if there is any error.
     */
    @Override
    public Response getSpecificAssociation(Long tenantId, Long permissionId, Long roleId, Long userId, boolean isLogicalConjunction) {
        try {
            SystemLinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(tenantId, permissionId, roleId, userId, isLogicalConjunction);
            return Response.ok(linkedAuthorizationBusinessService.getSpecificAssociation(filter)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Gets the information of a Linked Authorization which will be found using the id.
     * @param id to be searched for
     * @return a paginated response with the requested information. 200 code message if success,
     *      404 if role is not found, 500 code message if there is any error.
     */
    @Override
    public Response getAssociationById(Long id) {
        try{
            log.info("Will search for a specific role with the following id {}.", id);
            SystemLinkedAuthorization systemAssociation = linkedAuthorizationBusinessService.getAssociationById(id);
            return Response.ok(systemAssociation).build();
        } catch (LinkedAuthorizationNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Delete request which will delete the given id linked authorization information
     *
     * @param id record to be deleted
     * @return 200 code message if success, 404 if role is not found, 500 code message if there is any error.
     */
    @Override
    public Response deleteAssociation(long id) {
        try {
            log.info("Will delete a specific tenancy association with the following id {}.", id);
            linkedAuthorizationBusinessService.getAssociationById(id);
            linkedAuthorizationBusinessService.deleteAssociation(id);
        } catch (LinkedAuthorizationNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
        return Response.ok().build();
    }

    /**
     * Delete request which will delete ALL linked authorization information
     * that exists for a tenant and user (Both informed as parameter).
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return 200 code message if success, 404 if no associations were found,
     * 400 if either tenant or user were not informed,
     * 500 code message in case of any other error.
     */
    @Override
    public Response deleteAssociations(Long tenantId, Long userId) {
        try {
            log.info("Will delete All tenancy association with the following tenant {} and user {}.",
                    tenantId, userId);
            boolean status = linkedAuthorizationBusinessService.deleteAssociations(tenantId, userId);
            if (!status) {
                return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
            }
        } catch (LinkedAuthorizationException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
        return Response.ok().build();
    }

    /**
     * Inserts the given linked authorization information, wither creates a new record or updated one already existent one, depending
     * if the given linked authorization has an id or not.
     * @param association information to be update or created.
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 404 if role is not found, 500 code message if there is any error.
     */
    @Override
    public Response saveAssociation(LinkedAuthorization association) {
        try {
            log.info("New association to be created/updated it's on it's way!");
            linkedAuthorizationBusinessService.save(new io.radien.ms.rolemanagement.entities.LinkedAuthorization(association));
            return Response.ok().build();
        } catch (LinkedAuthorizationNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Check if exists LinkedAuthorization for a specific tenant id, permission id, role id or user id.
     * @param tenantId to be find.
     * @param permissionId to be find.
     * @param roleId to be find.
     * @param userId to be find
     * @param isLogicalConjunction true if the value to be searched must be exactly as it is given
     *                     or false if it must only contain such value.
     * @return 200 code message if success, 404 if not found, 500 code message
     *          if there is a system error.
     */
    @Override
    public Response existsSpecificAssociation(Long tenantId, Long permissionId, Long roleId, Long userId, boolean isLogicalConjunction) {
        try {
            SystemLinkedAuthorizationSearchFilter filter = new LinkedAuthorizationSearchFilter(tenantId, permissionId, roleId, userId, isLogicalConjunction);
            boolean exist = linkedAuthorizationBusinessService.existsSpecificAssociation(filter);
            return exist ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent linked Authorizations.
     */
    @Override
    public Response getTotalRecordsCount() {
        try {
            return Response.ok(linkedAuthorizationBusinessService.getTotalRecordsCount()).build();
        } catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Retrieve all roles related to a specific user.
     * Optionally tenant identifier may be informed to
     * improve the filtering process.
     * @param userId User identifier
     * @param tenantId Tenant Identifier (Optional parameter)
     */
    @Override
    public Response getRoles(Long userId, Long tenantId) {
        if (userId == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        try {
            return Response.ok(linkedAuthorizationBusinessService.getRolesByUserAndTenant(
                    userId, tenantId)).build();
        } catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Check if a user has a specific Role (Optionally, under a specific tenant)
     * @param userId User Identifier
     * @param roleName Role Name
     * @param tenantId Tenant Identifier
     * @return Http status OK (200) containing boolean value, otherwise Http status 500
     */
    @Override
    public Response isRoleExistentForUser(Long userId, String roleName, Long tenantId) {
        if (userId == null || roleName == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        try {
            return Response.ok(linkedAuthorizationBusinessService.isRoleExistentForUser(
                    userId, tenantId, roleName)).build();
        }
        catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Check if a user has a specific Role (Optionally, under a specific tenant)
     * @param userId User Identifier
     * @param roleName Role Name list
     * @param tenantId Tenant Identifier
     * @return Http status OK (200) containing boolean value, otherwise Http status 500
     */
    @Override
    public Response checkPermissions(Long userId, List<String> roleName, Long tenantId) {
        if (userId == null || roleName == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        try {
            return Response.ok(linkedAuthorizationBusinessService.checkPermissions(
                    userId, tenantId, roleName)).build();
        }
        catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }
}
