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
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Bruno Gama
 */
@Path("linkedauthorization")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface LinkedAuthorizationResourceClient {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAssociations(@DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                                       @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSpecificAssociation(@QueryParam("tenantId") Long tenantId,
                                           @QueryParam("permissionId") Long permissionId,
                                           @QueryParam("roleId") Long roleId,
                                           @QueryParam("userId") Long userId,
                                           @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAssociationById(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    public Response deleteAssociation(@NotNull @PathParam("id") long id);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveAssociation(LinkedAuthorization association);

    @GET
    @Path("/exists")
    Response existsSpecificAssociation(@QueryParam("tenantId") Long tenantId,
                                        @QueryParam("permissionId") Long permissionId,
                                        @QueryParam("roleId") Long roleId,
                                        @QueryParam("userId") Long userId,
                                        @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    @GET
    @Path("/countRecords")
    public Response getTotalRecordsCount();
}
