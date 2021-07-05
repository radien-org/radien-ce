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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.GlobalHeaders;
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
 * Active Tenant REST requests
 *
 * @author Bruno Gama
 */
@Path("/activeTenant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface ActiveTenantResourceClient {

    /**
     * Gets all the active tenants information into a paginated mode and return those information to the user.
     * @param search name description for some active tenant
     * @param pageNo of the requested information. Where the active tenant is.
     * @param pageSize total number of pages returned in the request.
     * @param sortBy sort filter criteria.
     * @param isAscending ascending filter criteria.
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
     * Gets a list of requested active tenants based on some filtered information
     * @param userId to be searched for
     * @param tenantId of the tenant to be searched
     * @param isTenantActive should the values be exact to the given ones
     * @param isLogicalConjunction in case of true query will use an and in case of false query will use a or
     * @return 200 response code in case of success or 500 in case of any issue
     */
    @GET
    @Path("/get")
    public Response get(@QueryParam("userId") Long userId, @QueryParam("tenantId") Long tenantId,
                        @QueryParam("tenantName") String tenantName,
                        @DefaultValue("false") @QueryParam("isTenantActive") boolean isTenantActive,
                        @DefaultValue("false") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Gets active tenant based on the given id
     * @param id to be searched for
     * @return 200 code message in case of success or 500 in case of any error
     */
    @GET
    @Path("/{id}")
    public Response getById(@NotNull @PathParam("id") Long id);

    /**
     * Gets active tenant based on the given id
     * @param userId to be searched for
     * @param tenantId to be searched for
     * @return 200 code message in case of success or 500 in case of any error
     */
    @GET
    @Path("/{userId}/{tenantId}")
    public Response getByUserAndTenant(@NotNull @PathParam("userId") Long userId, @NotNull @PathParam("tenantId") Long tenantId);

    /**
     * Requests to a active tenant be deleted by given his id
     * @param id of the active tenant to be deleted
     * @return a response with true or false based on the success or failure of the deletion
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Requests to delete active tenants taking in account the following parameters
     * @param tenantId tenant id of the active tenant to be deleted
     * @param userId user id of the active tenant to be deleted
     * @return a response with true or false based on the success or failure of the deletion
     */
    @DELETE
    @Path("/{tenantId}/{userId}")
    public Response delete(@NotNull @PathParam("tenantId") long tenantId,
                           @NotNull @PathParam("userId") long userId);

    /**
     * Method to request a creation of a active tenant
     * @param activeTenant information to be created
     * @return a response with true or false based on the success or failure of the creation
     */
    @POST
    public Response create(ActiveTenant activeTenant);

    /**
     * Method to update a requested active tenant
     * @param id of the active tenant to be updated
     * @param activeTenant information to be update
     * @return a response with true or false based on the success or failure of the update
     */
    @PUT
    @Path("/{id}")
    public Response update(@NotNull @PathParam("id") long id, ActiveTenant activeTenant);

    /**
     * Validates if specific requested active Tenant exists
     * @param userId to be found
     * @param tenantId to be found
     * @return response true if it exists
     */
    @GET
    @Path("/exists/{userId}/{tenantId}")
    public Response exists(@NotNull @PathParam("userId") Long userId, @NotNull @PathParam("tenantId") Long tenantId);
}
