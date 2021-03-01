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

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.exception.RoleErrorCodeMessage;
import io.radien.ms.rolemanagement.client.services.RoleResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author Bruno Gama
 */

@Path("role")
@RequestScoped
public class RoleResource implements RoleResourceClient {

    @Inject
    private RoleBusinessService roleBusinessService;

    private static final Logger log = LoggerFactory.getLogger(RoleResource.class);

    /**
     * Gets all the role information into a paginated mode and return those information to the user.
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
     * error.
     */
    @Override
    public Response getAll(int pageNo, int pageSize) {
        try {
            log.info("Will get all the role information I can find!");
            return Response.ok(roleBusinessService.getAll(pageNo, pageSize)).build();
        } catch(Exception e) {
            return getGenericError(e);
        }
    }

    /**
     * Retrieve all the information which has a specific name or description.
     * @param name to be find.
     * @param description to be find.
     * @param isExact true if the value to be searched must be exactly as it is given
     *                or false if it must only contain such value.
     * @param isLogicalConjunction true if the search between the values should be and or false if it should be or.
     * @return a paginated response with the requested information. 200 code message if success, 500 code message
     * if there is any error.
     */
    @Override
    public Response getSpecificRoles(String name, String description, boolean isExact, boolean isLogicalConjunction) {
        try {
            log.info("Will search for a specific role with the name {}, description {}. With the following criteria: " +
                    "Values must be exact: {}; Is a logical conjunction: {}", name, description, isExact, isLogicalConjunction);
            SystemRoleSearchFilter filter = new RoleSearchFilter(name, description, isExact, isLogicalConjunction);
            return Response.ok(roleBusinessService.getSpecificRoles(filter)).build();
        } catch (Exception e) {
            return getGenericError(e);
        }
    }

    /**
     *  Gets the information of a role which will be found using the id.
     *
     * @param id to be searched for
     * @return a paginated response with the requested information. 200 code message if success,
     * 404 if role is not found, 500 code message if there is any error.
     */
    @Override
    public Response getById(Long id) {
        try{
            log.info("Will search for a specific role with the following id {}.", id);
            SystemRole systemRole = roleBusinessService.getById(id);
            return Response.ok(systemRole).build();
        } catch (RoleNotFoundException e) {
            return getRoleNotFoundException();
        } catch (Exception e) {
            return getGenericError(e);
        }
    }

    /**
     * Delete request which will delete the given id role information
     *
     * @param id record to be deleted
     * @return 200 code message if success, 404 if role is not found, 500 code message if there is any error.
     */
    @Override
    public Response delete(long id) {
        try {
            log.info("Will delete a specific role with the following id {}.", id);
            roleBusinessService.getById(id);
            roleBusinessService.delete(id);
        } catch (RoleNotFoundException e) {
            return getRoleNotFoundException();
        } catch (Exception e){
            return getGenericError(e);
        }
        return Response.ok().build();
    }

    /**
     * Inserts the given role information, wither creates a new record or updated one already existent one, depending
     * if the given role has an id or not.
     *
     * @param role information to be update or created.
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 404 if role is not found, 500 code message if there is any error.
     */
    @Override
    public Response save(Role role) {
        try {
            log.info("New information to be created/updated it's on it's way!");
            roleBusinessService.save(new io.radien.ms.rolemanagement.entities.Role(role));
            return Response.ok().build();
        } catch (RoleNotFoundException e) {
            return getRoleNotFoundException();
        } catch (UniquenessConstraintException e) {
            return getInvalidRequestResponse(e);
        } catch (Exception e) {
            return getGenericError(e);
        }
    }

    /**
     * Validates if specific requested role exists
     * @param id to be searched
     * @param name to be searched
     * @return 200 status code message if it exists or 500 in case of any issue
     */
    @Override
    public Response exists(Long id, String name) {
        try {
            log.info("There is a validation to be done! I'm going to validate if the role id {}, or role name {} exists"
                    , id, name);
            if(roleBusinessService.exists(id, name)) {
                return Response.ok().build();
            }
            return getRoleNotFoundException();
        } catch (RoleNotFoundException e) {
            return getRoleNotFoundException();
        } catch (Exception e) {
            return getGenericError(e);
        }
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

    /**
     * Generic error exception. Launches a 500 Error Code to the user.
     * @param e exception to be throw
     * @return code 500 message Generic Exception
     */
    private Response getGenericError(Exception e) {
        String message = RoleErrorCodeMessage.GENERIC_ERROR.toString();
        log.error(message, e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }

    /**
     * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
     * @return code 100 message Resource not found.
     */
    private Response getRoleNotFoundException() {
        String message = RoleErrorCodeMessage.RESOURCE_NOT_FOUND.toString();
        log.error(message);
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }
}
