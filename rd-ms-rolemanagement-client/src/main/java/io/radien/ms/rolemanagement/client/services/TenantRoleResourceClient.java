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

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Base contract for TenantRole endpoints
 * @author Newton Carvalho
 */
@Path("tenantrole")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TenantRoleResourceClient {

    /**
     * Retrieves TenantRole association using pagination approach
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GET
    public Response getAll(@DefaultValue("0")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    /**
     * Retrieves TenantRole associations
     * @param tenantId
     * @param roleId
     * @param isLogicalConjunction
     * @return
     */
    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecific(@QueryParam("tenantId") Long tenantId,
                                @QueryParam("roleId") Long roleId,
                                @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Retrieves a TenantRole association
     * @param id
     * @return
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    /**
     * Delete a Tenant Role association
     * @param id
     * @return
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Create a Tenant Role association
     * @param tenantRole
     * @return
     */
    @POST
    public Response save(TenantRole tenantRole);

    @GET
    @Path("/exists/{tenantId}/{roleId}")
    Response exists(@QueryParam("tenantId") Long tenantId,
                    @QueryParam("roleId") Long roleId);

//    /**
//     * Retrieves the Role that exists for User (optionally for a specific Tenant)
//     * @param userId User identifier (Mandatory)
//     * @param tenantId Tenant identifier (Optional parameter)
//     * @return
//     */
//    @GET
//    @Path("/roles/user/{userId}")
//    Response getRoles(@PathParam("userId") Long userId,
//                      @QueryParam("tenantId") Long tenantId);

    /**
     * Retrieves the Permissions that exists for Tenant
     * and a Role (and optionally for a specific user)
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param userId User identifier (Optional parameter)
     * @return
     */
    @GET
    @Path("/permissions/tenant/{tenantId}/role/{roleId}")
    Response getPermissions(@PathParam("tenantId") Long tenantId,
                           @PathParam("roleId") Long roleId,
                           @QueryParam("userId") Long userId);

    /**
     * Retrieves the Tenants for which a user is associated
     * (Optionally for a specific Role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional parameter)
     * @return
     */
    @GET
    @Path("/tenants/user/{userId}")
    Response getTenants(@PathParam("userId") Long userId, @QueryParam("roleId") Long roleId);

    /**
     * Verifies if a Role exists for a User (Optionally for a Tenant)
     * @param userId User identifier (Mandatory)
     * @param roleName Name (unique) that identifies a Role (Mandatory)
     * @param tenantId Tenant identifier (Optional)
     * @return
     */
    @GET
    @Path("/exists/role")
    Response isRoleExistentForUser(@QueryParam("userId") Long userId,
                                   @QueryParam("roleName") String roleName,
                                   @QueryParam("tenantId") Long tenantId);

    /**
     * Verifies if a Permission exists for a User (Optionally for a Tenant)
     * @param userId User identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @param tenantId Tenant identifier (Optional)
     * @return
     */
    @GET
    @Path("/exists/permission")
    Response isPermissionExistentForUser(@NotNull @QueryParam("userId") Long userId,
                                         @NotNull @QueryParam("permissionId") Long permissionId,
                                         @QueryParam("tenantId") Long tenantId);

    /**
     * Assign a User for a Tenant under a specific Role
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param userId User identifier
     * @return
     */
    @POST
    @Path("/tenant/{tenantId}/role/{roleId}/user/{userId}")
    Response assignUser(Long tenantId, Long roleId, Long userId);

    /**
     * Unassign a User for a Tenant under a specific Role
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param userId User identifier
     * @return
     */
    @DELETE
    @Path("/tenant/{tenantId}/role/{roleId}/user/{userId}")
    Response unassignUser(Long tenantId, Long roleId, Long userId);
}
