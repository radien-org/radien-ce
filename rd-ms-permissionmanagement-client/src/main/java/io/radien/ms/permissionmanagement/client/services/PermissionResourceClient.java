/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.ms.permissionmanagement.client.services;

import io.radien.ms.permissionmanagement.client.entities.GlobalHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

/**
 * Contract for Rest Service Client regarding Action domain object
 * @author Newton Carvalho
 */
@Path("permission")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface PermissionResourceClient {

    /**
     * Retrieves a page object containing permissions that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL permissions
     * @param search search parameter for matching permissions (optional).
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return In case of successful operation returns OK (http status 200)
     * and the page object (filled or not).
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    public Response getAll(@QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    /**
     * Finds all permissions that matches a search filter
     * @param name permission name
     * @param action action id
     * @param resource resource id
     * @param ids permission ids to be found
     * @param isExact indicates if the match is for approximated value or not
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns 200 (http status) and the collection (filled or not).
     * Otherwise, in case of operational error, returns 500
     */
    @GET
    @Path("find")
    public Response getPermissions(@QueryParam("name") String name,
                             @QueryParam("action") Long action,
                             @QueryParam("resource") Long resource,
                             @QueryParam("ids") List<Long> ids,
                             @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                             @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Retrieves an permission by its identifier
     * @param id permission identifier
     * @return If permission exists returns 200 status (and the correspondent object)
     * Otherwise, if does not exist, return 404 status
     * In case of operational error return 500 status
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    /**
     * Deletes an permission by its identifier
     * @param id permission identifier
     * @return Returns 200 status, Otherwise, in case of operational error return 500 status
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Saves an permission (Creation or Update).
     * @param permission permission to be created or update
     * @return Http status 200 in case of successful operation.
     * Bad request (404) in case of trying to create an permission with repeated description.
     * Internal Server Error (500) in case of operational error
     */
	@POST
	public Response save(io.radien.ms.permissionmanagement.client.entities.Permission permission);

    /**
     * Rest endpoint operation to assign an action to a permission
     * @param permissionId permission identifier
     * @param actionId action identifier
     * @param resourceId resource identifier
     * @return OK (200): in case of successful operation.
     * Bad Request (404) and issue description: If the association could not be perform for not attending
     * some business rules.
     * Internal server error (500): In case of some error during processing
     */
	@POST
	@Path("/{permissionId}/action/{actionId}/resource/{resourceId}")
	public Response associate(@NotNull @PathParam("permissionId") long permissionId,
					   @NotNull @PathParam("actionId") long actionId,
                       @NotNull @PathParam("resourceId") long resourceId);

    /**
     * Rest endpoint operation to (un)assign an action to a permission
     * @param permissionId permission identifier
     * @return OK (200): in case of successful operation.
     * Bad Request (404) and issue description: If the association could not be perform for not attending
     * some business rules.
     * Internal server error (500): In case of some error during processing
     */
	@POST
	@Path("/{permissionId}/dissociation")
	public Response dissociate(@NotNull @PathParam("permissionId") long permissionId);

    /**
     * Validates if Permission exists for a referred Id (or alternatively taking in account name)
     * @param id Identifier to guide the search be searched (Primary parameter)
     * @param name Permission name, an alternative parameter to be used (only if Id is omitted)
     * @return 200: If Permission exists
     *     	   404: If Permission does not exist
     *         400: (Bad Request): None expected parameter informed
     *         500: In case of any other issue
     */
    @GET
    @Path("/exists")
    public Response exists(@QueryParam("id") Long id, @QueryParam("name") String name);

    /**
     * Validates if an user has a specific Permission (Combination of action name + resource name)
     * @param action Action name
     * @param resource Resource name
     * @return system permission
     */
    @GET
    @Path("/{action}/{resource}")
    public Response hasPermission(@NotNull @PathParam("action") String action,
                                  @NotNull @PathParam("resource") String resource);
    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent permissions.
     */
    @GET
    @Path("/countRecords")
    public Response getTotalRecordsCount();
}