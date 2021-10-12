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

import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.ms.rolemanagement.client.entities.GlobalHeaders;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import javax.ws.rs.HEAD;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

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
 * Tenant Role REST requests and services
 * @author Bruno Gama
 */
@Path("tenantrole")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TenantRoleResourceClient {

    /**
     * Retrieves TenantRole association using pagination approach
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    /**
     * Retrieves TenantRole associations that met the following parameter
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns OK (http status 200)
     * and a Collection containing TenantRole associations.<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecific(@QueryParam("tenantId") Long tenantId,
                                @QueryParam("roleId") Long roleId,
                                @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Retrieves a Tenant Role association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") Long id);

    /**
     * Retrieve the association Id ({@link SystemTenantRole#getId()}) using the combination of tenant
     * and role as parameters
     * @param tenant tenant identifier
     * @param role role identifier
     * @return Response OK (200) with the retrieved id (if exists). If not exist will return 404 status.
     * In case of insufficient params (tenant or role not informed) It will return 400 status.
     * For any other kind of (unpredictable) error this endpoint will return status 500
     */
    @GET
    @Path("/id")
    public Response getIdByTenantRole(@QueryParam("tenant") Long tenant, @QueryParam("role") Long role);

    /**
     * Deletes a Tenant Role association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 400 if association could not be found or if the association is attached to other entities
     * (i.e TenantRoleUser, TenantRolePermission, etc), 500 code message if there is any error.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id);

    /**
     * Create a TenantRole association
     * @param tenantRole bean that corresponds to TenantRole association to be created
     * @return 200 code message if success, 400 in case of duplication (association already
     * existing with the same parameter) or absence of information (tenant or role not existing),
     * 500 code message if there is any error.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response save(TenantRole tenantRole);

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return Responds with 204 http status if association exists, 404 (NOT FOUND) otherwise.
     * Response 500 in case of any other error.
     */
    @HEAD
    @Path("/{tenantId}/{roleId}")
    Response exists(@PathParam("tenantId") Long tenantId,
                    @PathParam("roleId") Long roleId);

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return Response OK with List containing permissions. Response 500 in case of any other error.
     */
    @GET
    @Path("/permissions/tenant/{tenantId}/role/{roleId}")
    Response getPermissions(@PathParam("tenantId") Long tenantId,
                           @PathParam("roleId") Long roleId,
                           @QueryParam("userId") Long userId);

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return Response OK with List containing tenants. Response 500 in case of any other error.
     */
    @GET
    @Path("/tenants/user/{userId}")
    Response getTenants(@PathParam("userId") Long userId, @QueryParam("roleId") Long roleId);

    /**
     * Retrieves the Roles for which a User is associated under a Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return Response OK if operation concludes with success.
     * Response 500 in case of error
     */
    @GET
    @Path("/rolesUserTenant")
    Response getRolesForUserTenant(@QueryParam("userId") Long userId, @QueryParam("tenantId") Long tenantId);

    /**
     * Check if Role exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param roleName Role name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if role is associated to an User, otherwise false).
     * Response 404 in case of absence of parameter like user identifier or role name.
     * Response 500 in case of any error
     */
    @GET
    @Path("/exists/role")
    Response isRoleExistentForUser(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("tenantId") Long tenantId);

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
    @GET
    @Path("/exists/roles")
    Response isAnyRoleExistentForUser(@QueryParam("userId") Long userId,
                                      @QueryParam("roleNames") List<String> roleNames,
                                      @QueryParam("tenantId") Long tenantId);

    /**
     * Check if Permission exists for a User (Optionally under a Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return Response OK containing a boolean value (true if permission is associated to an User, otherwise false).
     * Response 404 in case of absence of parameter like user identifier or permission identifier.
     * Response 500 in case of any error
     */
    @GET
    @Path("/exists/permission")
    Response isPermissionExistentForUser(@QueryParam("userId") Long userId,
                                         @QueryParam("permissionId") Long permissionId,
                                         @QueryParam("tenantId") Long tenantId);

}