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
package io.radien.ms.rolemanagement.client.services;

import io.radien.ms.rolemanagement.client.entities.GlobalHeaders;
import io.radien.ms.rolemanagement.client.entities.Role;
import javax.ws.rs.PUT;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Role REST requests and services
 * @author Bruno Gama
 */
@Path("role")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface RoleResourceClient {

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
    @GET
    public Response getAll(@QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

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
    @GET
    @Path("/find")
    public Response getSpecificRoles(@QueryParam("name") String name,
                                     @QueryParam("description") String description,
                                     @QueryParam("ids") List<Long> ids,
                                     @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                                     @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Gets the information of a role which will be found using the id.
     *
     * @param id to be searched for
     * @return a paginated response with the requested information. 200 code message if success,
     * 404 if role is not found, 500 code message if there is any error.
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    /**
     * Delete request which will delete the given id role information
     *
     * @param id record to be deleted
     * @return 200 code message if success, 404 if role is not found, 500 code message if there is any error.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Inserts the given role information
     *
     * @param role information to be created.
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 500 code message if there is any error.
     */
    @POST
    public Response create(Role role);

    /**
     * Updates the given role information
     *
     * @param role information to be update
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 404 if role is not found, 500 code message if there is any error.
     */
    @PUT
    @Path("/{id}")
    public Response update(@NotNull @PathParam("id") long id, Role role);

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent roles.
     */
    @GET
    @Path("/countRecords")
    public Response getTotalRecordsCount();
}
