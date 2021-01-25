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
import io.radien.api.service.role.RoleServiceAccess;
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

    // TODO: Bruno Gama - Handle Exceptions

    @Inject
    private RoleServiceAccess roleServiceAccess;

    private static final Logger log = LoggerFactory.getLogger(RoleResource.class);


    @Override
    public Response getAll(int pageNo, int pageSize) {
        try {
            return Response.ok(roleServiceAccess.getAll(pageNo, pageSize)).build();
        } catch(Exception e) {
            return getGenericError(e);
        }
    }

    @Override
    public Response getSpecificRoles(String name, String description, boolean isExact, boolean isLogicalConjunction) {
        try {
            SystemRoleSearchFilter filter = new RoleSearchFilter(name, description, isExact, isLogicalConjunction);
            return Response.ok(roleServiceAccess.getSpecificRoles(filter)).build();
        } catch (Exception e) {
            return getGenericError(e);
        }
    }


    @Override
    public Response getById(Long id) {
        try{
            SystemRole systemRole = roleServiceAccess.get(id);
            return Response.ok(systemRole).build();
        } catch (RoleNotFoundException e) {
            return getRoleNotFoundException();
        } catch (Exception e) {
            return getGenericError(e);
        }
    }

    @Override
    public Response delete(long id) {
        try {
            roleServiceAccess.get(id);
            roleServiceAccess.delete(id);
        } catch (RoleNotFoundException e) {
            return getRoleNotFoundException();
        } catch (Exception e){
            return getGenericError(e);
        }
        return Response.ok().build();
    }

    @Override
    public Response save(Role role) {
        try {
            roleServiceAccess.save(new io.radien.ms.rolemanagement.entities.Role(role));
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
