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
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorizationSearchFilter;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.ms.rolemanagement.client.exception.LinkedAuthorizationErrorCodeMessage;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
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
            return getGenericError(e);
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
            return getGenericError(e);
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
            return getRoleNotFoundException();
        } catch (Exception e) {
            return getGenericError(e);
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
            return getRoleNotFoundException();
        } catch (Exception e){
            return getGenericError(e);
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
            return getRoleNotFoundException();
        } catch (UniquenessConstraintException e) {
            return getInvalidRequestResponse(e);
        } catch (Exception e) {
            return getGenericError(e);
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
            return getGenericError(e);
        }
    }

    /**
     * Generic error exception. Launches a 500 Error Code to the user.
     * @param e exception to be throw
     * @return code 500 message Generic Exception
     */
    private Response getGenericError(Exception e) {
        String message = LinkedAuthorizationErrorCodeMessage.GENERIC_ERROR.toString();
        log.error(message, e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }

    /**
     * Generic error exception to when the linked authorization could not be found in DB. Launches a 404 Error Code to the user.
     * @return code 100 message Resource not found.
     */
    private Response getRoleNotFoundException() {
        String message = LinkedAuthorizationErrorCodeMessage.RESOURCE_NOT_FOUND.toString();
        log.error(message);
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }

    /**
     * Invalid Request error exception. Launches a 400 Error Code to the user.
     * @param e exception to be throw
     * @return code 400 message Generic Exception
     */
    private Response getInvalidRequestResponse(UniquenessConstraintException e) {
        String message = e.getMessage();
        log.error(message);
        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
    }
}