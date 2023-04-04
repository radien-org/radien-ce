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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.ms.tenantmanagement.client.entities.GlobalHeaders;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import javax.ws.rs.HEAD;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Tenant REST requests
 *
 * @author Santana
 */
@Path("tenant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TenantResourceClient {

    /**
     * Gets all the tenant information into a paginated mode and return those information to the user.
     * @param search name description for some tenant
     * @param pageNo of the requested information. Where the tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.	 *
     * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
     * error.
     */
    @GET
    public Response getAll(@QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    /**
     * Gets a list of requested tenants based on some filtered information
     * @param name to be searched for
     * @param type of the tenant to be searched
     * @param ids list of ids to be search for
     * @param isExact should the values be exact to the given ones
     * @param isLogicalConjunction in case of true query will use an and in case of false query will use a or
     * @return 200 response code in case of success or 500 in case of any issue
     */
    @GET
    @Path("/find")
    public Response get(@QueryParam("name") String name, @QueryParam("tenantType") String type,
                        @QueryParam("ids") List<Long> ids,
                        @DefaultValue("false") @QueryParam("isExact") boolean isExact,
                        @DefaultValue("false") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Method to get all the requested tenant children tenants
     * @param id of the parent tenant
     * @return a list of all the tenant children
     */
    @GET
    @Path("/{id}/children")
    public Response getChildren(@PathParam("id") Long id);

    /**
     * Gets tenant based on the given id
     * @param id to be searched for
     * @return 200 code message in case of success or 500 in case of any error
     */
    @GET
    @Path("/{id}")
    public Response getById(@NotNull @PathParam("id") Long id);

    /**
     * Requests to a tenant be deleted by given his id
     * @param id of the tenant to be deleted
     * @return a response with true or false based on the success or failure of the deletion
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Request to delete a tenant and all his children
     * @param id of the tenant to be deleted
     * @return a response with true or false based on the success or failure of the deletion
     */
    @DELETE
    @Path("/hierarchy/{id}")
    public Response deleteTenantHierarchy(@NotNull @PathParam("id") long id);

    /**
     * Method to request a creation of a tenant
     * @param tenant information to be created
     * @return a response with true or false based on the success or failure of the creation
     */
    @POST
    public Response create(Tenant tenant);

    /**
     * Method to update a requested tenant
     * @param id of the tenant to be updated
     * @param contract information to be update
     * @return a response with true or false based on the success or failure of the update
     */
    @PUT
    @Path("/{id}")
    public Response update(@NotNull @PathParam("id") long id,Tenant contract);

    /**
     * Validates if specific requested Tenant exists
     * @param id to be searched
     * @return response 204 if tenant exists. 404 if do not exist.
     * 500 in case of any other processing error.
     */
    @HEAD
    @Path("/{id}")
    public Response exists(@NotNull @PathParam("id") Long id);

}
