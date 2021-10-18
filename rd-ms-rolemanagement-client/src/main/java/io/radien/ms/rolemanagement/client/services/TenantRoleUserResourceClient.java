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
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import java.util.Collection;
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
 * Tenant Role User REST requests and services
 * @author Bruno Gama
 */
@Path("tenantroleuser")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TenantRoleUserResourceClient {

    /**
     * Retrieves TenantRoleUser association using pagination approach
     * (in other words, retrieves the Users associations that exist for a TenantRole)
     * @param tenantId tenant identifier for a TenantRole
     * @param roleId role identifier for a TenantRole
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    Response getAll(@QueryParam("tenantId") Long tenantId,
                    @QueryParam("roleId") Long roleId,
                    @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                    @DefaultValue("10") @QueryParam("pageSize") int pageSize);


    /**
     * Retrieves TenantRoleUser association (Ids) using pagination approach
     * (in other words, retrieves the Users associations that exist for a TenantRole)
     * @param tenantId tenant identifier for a TenantRole
     * @param roleId role identifier for a TenantRole
     * @param pageNo page number
     * @param pageSize page size
     * @return In case of successful operation returns OK (http status 200)
     * and a Page containing TenantRole associations Ids (Chunk/Portion compatible
     * with parameter Page number and Page size).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    @Path("/userIds")
    Response getAllUserIds(@QueryParam("tenantId") Long tenantId,
                           @QueryParam("roleId") Long roleId,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    /**
     * Deletes a Tenant Role User association using the id as search parameter.
     * @param id Tenant Role User id association to guide the search process
     * @return 200 code message in case of success (Tenant Role User association found)
     * 400 if tenant role User association could not be found ,
     * 500 code message if there is any error.
     */
    @DELETE
    @Path("/{id}")
    Response delete(@PathParam("id") long id);

    /**
     * Assign/associate/add permission to a TenantRole domain
     * The association will always be under a specific role
     * @param tenantRoleUser represents the association between Tenant, Role and User
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response assignUser(TenantRoleUser tenantRoleUser);

    /**
     * (Un)Assign/Dissociate/remove user from a TenantRole domain
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleIds Roles identifiers
     * @param userId User identifier (Mandatory)
     * @return Response OK if operation concludes with success.
     * Response status 400 in case of violations regarding business rules
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @DELETE
    Response unAssignUser(@QueryParam("tenantId") Long tenantId,
                          @QueryParam("roleIds") Collection<Long> roleIds,
                          @QueryParam("userId") Long userId);

    /**
     * Retrieves TenantRole User associations that met the following parameter
     * @param tenantRoleId TenantRole identifier
     * @param userId User identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns OK (http status 200)
     * and a Collection containing TenantRole associations.<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    @Path("/find")
    Response getSpecific(@QueryParam("tenantRoleId") Long tenantRoleId,
                         @QueryParam("userId") Long userId,
                         @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Retrieves a Tenant Role User using the id as search parameter.
     * @param id Tenant Role User id to guide the search process
     * @return 200 code message in case of success (Tenant Role association found)
     * 404 if association could not be found, 500 code message if there is any error.
     */
    @GET
    @Path("/{id}")
    Response getById(@PathParam("id") Long id);

    /**
     * Updates a TenantRoleUser
     * @param id corresponds to the identifier of the TenantRoleUser to be updated
     * @param tenantRoleUser instance containing the information to be updated
     * @return Response OK if operation concludes with success.
     * Response status 404 in case of not existing a TenantRoleUser for the informed id,
     * Response status 400 in case of association already existing or
     * other consistency issues found.
     * Response 500 in case of any other error (i.e communication issue with REST client services)
     */
    @PUT
    @Path("/{id}")
    Response update(@PathParam("id") long id, TenantRoleUser tenantRoleUser);
}
