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
package io.radien.ms.rolemanagement.client.services;

import io.radien.ms.rolemanagement.client.entities.GlobalHeaders;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
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
    @Path("/all")
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
     * @return Response OK containing true (if association exists), false otherwise.
     * Response 500 in case of any other error.
     */
    @GET
    @Path("/exists/tenant/{tenantId}/role/{roleId}")
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

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @POST
    @Path("/assign/user/{userId}/tenant/{tenantId}/role/{roleId}")
    Response assignUser(@PathParam("tenantId") Long tenantId,
                        @PathParam("roleId") Long roleId,
                        @PathParam("userId") Long userId);

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier
     * @param userId User identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @DELETE
    @Path("/unassign/user/{userId}/tenant/{tenantId}")
    Response unassignUser(@PathParam("tenantId") Long tenantId,
                          @QueryParam("roleId") Long roleId,
                          @PathParam("userId") Long userId);

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @POST
    @Path("/assign/permission/{permissionId}/tenant/{tenantId}/role/{roleId}")
    Response assignPermission(@PathParam("tenantId") Long tenantId,
                              @PathParam("roleId") Long roleId,
                              @PathParam("permissionId") Long permissionId);

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @DELETE
    @Path("/unassign/permission/{permissionId}/tenant/{tenantId}/role/{roleId}")
    Response unassignPermission(@PathParam("tenantId") Long tenantId,
                                @PathParam("roleId") Long roleId,
                                @PathParam("permissionId") Long permissionId);
}
