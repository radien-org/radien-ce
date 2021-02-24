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
package io.radien.ms.permissionmanagement.client.services;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

/**
 * @author Newton Carvalho
 * Contract for Rest Service Client regarding Action domain object
 */
@Path("permission")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface PermissionResourceClient {

    @GET
    public Response getAll(@QueryParam("search") String search,
                            @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    @GET
    @Path("find")
    public Response getPermissions(@QueryParam("name") String name,
                             @QueryParam("action") Long action,
                             @QueryParam("resource") Long resource,
                             @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                             @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

	@POST
	public Response save(io.radien.ms.permissionmanagement.client.entities.Permission permission);

	@POST
	@Path("/{permissionId}/action/{actionId}/resource/{resourceId}")
	public Response associate(@NotNull @PathParam("permissionId") long permissionId,
					   @NotNull @PathParam("actionId") long actionId,
                       @NotNull @PathParam("resourceId") long resourceId);

	@POST
	@Path("/{permissionId}/dissociation")
	public Response dissociate(@NotNull @PathParam("permissionId") long permissionId);

    @GET
    @Path("/exists/{id}")
    public Response exists(@NotNull @PathParam("id") Long id);

}