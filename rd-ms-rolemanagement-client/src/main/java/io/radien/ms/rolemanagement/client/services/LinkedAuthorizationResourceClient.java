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
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
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
 * Linked authorization REST requests and services
 * @author Bruno Gama
 */
@Path("linkedauthorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface LinkedAuthorizationResourceClient {

    /**
     * Gets all the role information into a paginated mode and return those information to the user.
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of roles to be showed in each page.
     * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
     * error.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssociations(@DefaultValue("0")  @QueryParam("pageNo") int pageNo,
                                       @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    /**
     * Retrieve all the information which has a specific tenant id, permission id, role id or user id
     * @param tenantId to be find.
     * @param permissionId to be find.
     * @param roleId to be find.
     * @param isLogicalConjunction true if the value to be searched must be exactly as it is given
     *                     or false if it must only contain such value.
     * @return a paginated response with the requested information. 200 code message if success, 500 code message
     *          if there is any error.
     */
    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecificAssociation(@QueryParam("tenantId") Long tenantId,
                                           @QueryParam("permissionId") Long permissionId,
                                           @QueryParam("roleId") Long roleId,
                                           @QueryParam("userId") Long userId,
                                           @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Gets the information of a Linked Authorization which will be found using the id.
     * @param id to be searched for
     * @return a paginated response with the requested information. 200 code message if success,
     *      404 if role is not found, 500 code message if there is any error.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssociationById(@PathParam("id") Long id);

    /**
     * Delete request which will delete the given id linked authorization information
     *
     * @param id record to be deleted
     * @return 200 code message if success, 404 if role is not found, 500 code message if there is any error.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAssociation(@NotNull @PathParam("id") long id);

    /**
     * Delete request which will delete ALL linked authorization information
     * that exists for a Tenant and an User (Both informed as parameter).
     *
     * @param tenantId Tenant identifier
     * @param userId User identifier
     * @return 200 code message if success
     * 400 if neither tenant and user were informed, 500 code message if there is any error.
     */
    @DELETE
    Response deleteAssociations(@QueryParam("tenantId") Long tenantId,
                                @QueryParam("userId") Long userId);

    /**
     * Inserts the given linked authorization information, wither creates a new record or updated one already existent one, depending
     * if the given linked authorization has an id or not.
     * @param association information to be update or created.
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 404 if role is not found, 500 code message if there is any error.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveAssociation(LinkedAuthorization association);

    /**
     * Check if exists LinkedAuthorization for a specific tenant id, permission id, role id or user id.
     * @param tenantId to be find.
     * @param permissionId to be find.
     * @param roleId to be find.
     * @param userId to be find
     * @param isLogicalConjunction true if the value to be searched must be exactly as it is given
     *                     or false if it must only contain such value.
     * @return 200 code message if success, 404 if not found, 500 code message
     *          if there is a system error.
     */
    @GET
    @Path("/exists")
    Response existsSpecificAssociation(@QueryParam("tenantId") Long tenantId,
                                        @QueryParam("permissionId") Long permissionId,
                                        @QueryParam("roleId") Long roleId,
                                        @QueryParam("userId") Long userId,
                                        @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent linked Authorizations.
     */
    @GET
    @Path("/countRecords")
    public Response getTotalRecordsCount();

    /**
     * Retrieve all roles related to a specific user.
     * Optionally tenant identifier may be informed to
     * improve the filtering process.
     * @param userId User identifier
     * @param tenantId Tenant Identifier (Optional parameter)
     */
    @GET
    @Path("/roles")
    Response getRoles(@QueryParam("userId") @NotNull Long userId,
                      @QueryParam("tenantId") Long tenantId);

    /**
     * Check if a user has a specific Role (Optionally, under a specific tenant)
     * @param userId User Identifier
     * @param roleName Role Name
     * @param tenantId Tenant Identifier
     * @return Http status OK (200) containing boolean value, otherwise Http status 500
     */
    @GET
    @Path("/exists/role")
    Response isRoleExistentForUser(@NotNull @QueryParam("userId") Long userId,
                                   @NotNull @QueryParam("roleName") String roleName,
                                   @QueryParam("tenantId") Long tenantId);

    /**
     * Check if a user has a specific Role (Optionally, under a specific tenant)
     * @param userId User Identifier
     * @param roleName Role Name list
     * @param tenantId Tenant Identifier
     * @return Http status OK (200) containing boolean value, otherwise Http status 500
     */
    @GET
    @Path("/exists/checkPermissions")
    Response checkPermissions(@QueryParam("userId") Long userId,
                              @QueryParam("roleName") List<String> roleName,
                              @QueryParam("tenantId") Long tenantId);
}
