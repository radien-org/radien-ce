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
import io.radien.ms.rolemanagement.client.entities.TenantRolePermission;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

/**
 * Tenant Role Permission REST requests and services
 * @author Bruno Gama
 */
@Path("tenantrolepermission")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TenantRolePermissionResourceClient {

    /**
     * Retrieves TenantRolePermission association using pagination approach
     * (in other words, retrieves the Permissions associations that exist for a TenantRole)
     * @param tenantRoleId identifier for a TenantRole (Optional)
     * @param permissionId identifier for a permission (Optional)
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRolePermission associations (Chunk/Portion compatible
     * with parameter Page number and Page size).
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    Response getAll(@QueryParam("tenantRoleId") Long tenantRoleId,
                    @QueryParam("permissionId") Long permissionId,
                    @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                    @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                    @QueryParam("sortBy") List<String> sortBy,
                    @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    /**
     * Retrieves TenantRole Permission associations that met the following parameter
     * @param tenantRoleId TenantRole identifier
     * @param permissionId Permission identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns OK (http status 200)
     * and a Collection containing TenantRole associations.<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    @Path("/find")
    Response getSpecific(@QueryParam("tenantRoleId") Long tenantRoleId,
                         @QueryParam("permissionId") Long permissionId,
                         @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Retrieves a Tenant Role Permission using the id as search parameter.
     * @param id Tenant Role Permission id to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @GET
    @Path("/{id}")
    Response getById(@PathParam("id") Long id);

    /**
     * Deletes a Tenant Role Permission association using the id as search parameter.
     * @param id Tenant Role id association to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 400 if tenant role permission association could not be found ,
     * 500 code message if there is any error.
     */
    @DELETE
    @Path("/{id}")
    Response delete(@PathParam("id") long id);

    /**
     * Assign/associate/add permission to a TenantRole domain
     * The association will always be under a specific role
     * @param tenantRolePermission represents the association between Tenant, Role and Permission
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response assignPermission(TenantRolePermission tenantRolePermission);

    /**
     * Updates a TenantRolePermission
     * @param id corresponds to the identifier of the TenantRolePermission to be updated
     * @param tenantRolePermission instance containing the information to be updated
     * @return Response OK if operation concludes with success.
     * Response status 404 in case of not existing a TenantRolePermission for the informed id,
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @PUT
    @Path("/{id}")
    Response update(@PathParam("id") long id, TenantRolePermission tenantRolePermission);

    /**
     * (Un)Assign/Dissociate/remove permission from a TenantRole domain
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param permissionId Permission identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @DELETE
    Response unAssignPermission(@QueryParam("tenantId") Long tenantId,
                                @QueryParam("roleId") Long roleId,
                                @QueryParam("permissionId") Long permissionId);

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return Response OK with List containing permissions. Response 500 in case of any other error.
     */
    @GET
    @Path("/permissions")
    Response getPermissions(@QueryParam("tenantId") Long tenantId,
                            @QueryParam("roleId") Long roleId,
                            @QueryParam("userId") Long userId);
}
