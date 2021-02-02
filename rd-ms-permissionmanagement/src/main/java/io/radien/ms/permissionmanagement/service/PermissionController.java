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
package io.radien.ms.permissionmanagement.service;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.api.service.permission.PermissionServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.permissionmanagement.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/**
 * @author mawe
 *
 */
@Path("permission")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PermissionController {

	private Logger log = LoggerFactory.getLogger(PermissionController.class);

	@Inject
	private PermissionServiceAccess permissionServiceAccess;

	@GET
	public Response getAll(@QueryParam("search") String search,
						   @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
						   @DefaultValue("10") @QueryParam("pageSize") int pageSize,
						   @QueryParam("sortBy") List<String> sortBy,
						   @DefaultValue("true") @QueryParam("asc") boolean isAscending) {
		try {
			return Response.ok(permissionServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	@GET
	@Path("find")
	public Response getPermissions(@QueryParam("name") String name,
								   @DefaultValue("true") @QueryParam("isExact") boolean isExact,
								   @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction) {

		try {
			SystemPermissionSearchFilter filter = new PermissionSearchFilter(name,isExact,isLogicalConjunction);
			return Response.ok(permissionServiceAccess.getPermissions(filter)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	@GET
	@Path("/{id}")
	public Response getById(@PathParam("id") Long id) {
		try {
			SystemPermission systemPermission = permissionServiceAccess.get(id);
			if(systemPermission == null){
				return getResourceNotFoundException();
			}
			return Response.ok(systemPermission).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@NotNull @PathParam("id") long id) {
		try {
			permissionServiceAccess.delete(id);
		} catch (Exception e){
			return getGenericError(e);
		}
		return Response.ok().build();
	}

	@POST
	public Response save(io.radien.ms.permissionmanagement.client.entities.Permission permission) {
		try {
			permissionServiceAccess.save(new Permission(permission));
			return Response.ok().build();
		} catch (UniquenessConstraintException e) {
			return getInvalidRequestResponse(e);
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Invalid Request error exception. Launches a 400 Error Code to the user.
	 * @param e exception to be throw
	 * @return code 400 message Generic Exception
	 */
	private Response getInvalidRequestResponse(UniquenessConstraintException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the user.
	 * @param e exception to be throw
	 * @return code 500 message Generic Exception
	 */
	private Response getGenericError(Exception e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
	}

	/**
	 * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
	 * @return code 100 message Resource not found.
	 */
	private Response getResourceNotFoundException() {
		return Response.status(Response.Status.NOT_FOUND).
				entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
	}
}