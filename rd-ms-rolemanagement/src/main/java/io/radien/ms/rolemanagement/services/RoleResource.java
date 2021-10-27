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

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.services.RoleResourceClient;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Role resource that will request the service to validate the actions on
 * the db
 *
 * @author Bruno Gama
 */
@Path("role")
@RequestScoped
public class RoleResource implements RoleResourceClient {

    @Inject
    private RoleBusinessService roleBusinessService;

    private static final Logger log = LoggerFactory.getLogger(RoleResource.class);

    /**
     * Retrieves a page object containing roles that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL roles
     * @param search search parameter for matching roles (optional).
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return In case of successful operation returns OK (http status 200)
     * and the page object (filled or not).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @Override
    public Response getAll(String search,
                           int pageNo,
                           int pageSize,
                           List<String> sortBy,
                           boolean isAscending) {
        try {
            log.info("Will get all the role information I can find!");
            return Response.ok(roleBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
        } catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
    public Response getSpecificRoles(String name, String description, List<Long> ids, boolean isExact, boolean isLogicalConjunction) {
        try {
            log.info("Will search for a specific role with the name {}, description {}, ids {}. With the following criteria: " +
                    "Values must be exact: {}; Is a logical conjunction: {}", name, description, ids, isExact, isLogicalConjunction);
            SystemRoleSearchFilter filter = new RoleSearchFilter(name, description, ids, isExact, isLogicalConjunction);
            return Response.ok(roleBusinessService.getSpecificRoles(filter)).build();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e){
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
            roleBusinessService.save(new RoleEntity(role));
            return Response.ok().build();
        } catch (RoleNotFoundException e) {
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (UniquenessConstraintException e) {
            return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
            return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
        } catch (Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent roles.
     */
    @Override
    public Response getTotalRecordsCount() {
        try {
            return Response.ok(roleBusinessService.getTotalRecordsCount()).build();
        } catch(Exception e) {
            return GenericErrorMessagesToResponseMapper.getGenericError(e);
        }
    }
}
