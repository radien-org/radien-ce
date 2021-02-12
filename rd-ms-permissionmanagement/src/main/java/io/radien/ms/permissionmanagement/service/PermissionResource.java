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
import io.radien.ms.permissionmanagement.client.entities.AssociationStatus;
import io.radien.ms.permissionmanagement.client.services.PermissionResourceClient;
import io.radien.ms.permissionmanagement.model.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/**
 * @author Newton Carvalho
 * Controller implementation responsible for deal with CRUD
 * operations requests (CRUD) regarding Permission domain object
 */
@Path("permission")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PermissionResource implements PermissionResourceClient {

	private Logger log = LoggerFactory.getLogger(PermissionResource.class);

	@Inject
	private PermissionServiceAccess permissionServiceAccess;

	@Inject
	private PermissionBusinessService businessService;

	/**
	 * Retrieves a page object containing permissions that matches search parameter.
	 * In case of omitted (empty) search parameter retrieves ALL permissions
	 * @param search search parameter for matching permissions (optional).
	 * @param pageNo page number
	 * @param pageSize page size
	 * @param sortBy Sorting fields
	 * @param isAscending Defines if ascending or descending in relation of sorting fields
	 * @return In case of successful operation returns OK (http status 200)
	 * and the page object (filled or not).<br>
	 * Otherwise, in case of operational error, returns Internal Server Error (500)
	 */
	public Response getAll(String search,
						   int pageNo,
						   int pageSize,
						   List<String> sortBy,
						   boolean isAscending) {
		try {
			return Response.ok(permissionServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Finds all permissions that matches a name
	 * @param name permission name
	 * @param isExact indicates if the match is for approximated value or not
	 * @param isLogicalConjunction
	 * @return In case of successful operation returns 200 (http status)
	 * and the collection (filled or not).<br>
	 * Otherwise, in case of operational error, returns 500
	 */
	public Response getPermissions(String name,
								   boolean isExact,
								   boolean isLogicalConjunction) {

		try {
			SystemPermissionSearchFilter filter = new PermissionSearchFilter(name,isExact,isLogicalConjunction);
			return Response.ok(permissionServiceAccess.getPermissions(filter)).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Retrieves an permission by its identifier
	 * @param id permission identifier
	 * @return If permission exists returns 200 status (and the correspondent object)
	 * Otherwise, if does not exist, return 404 status
	 * In case of operational error return 500 status
	 */
	public Response getById(Long id) {
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

	/**
	 * Deletes an permission by its identifier
	 * @param id permission identifier
	 * @return Returns 200 status, Otherwise, in case of operational error return 500 status
	 */
	public Response delete(long id) {
		try {
			permissionServiceAccess.delete(id);
		} catch (Exception e){
			return getGenericError(e);
		}
		return Response.ok().build();
	}

	/**
	 * Saves an permission (Creation or Update).
	 * @param permission permission to be created or update
	 * @return Http status 200 in case of successful operation.<br>
	 * Bad request (404) in case of trying to create an permission with repeated description.<br>
	 * Internal Server Error (500) in case of operational error
	 */
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
	 * Rest endpoint operation to assign an action to a permission
	 * @param permissionId permission identifier
	 * @param actionId action identifier
	 * @return OK (200): in case of successful operation.<br>
	 * Bad Request (404) and issue description: If the association could not be perform for not attending
	 * some business rules.
	 * Internal server error (500): In case of some error during processing
	 */
	public Response associate(long permissionId,
							  long actionId) {
		try {
			AssociationStatus associationStatus = businessService.associate(permissionId, actionId);
			if (associationStatus.isOK()) {
				return Response.ok().build();
			}
			return Response.status(Response.Status.BAD_REQUEST).
					entity(associationStatus.getMessage()).build();
		} catch (Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Rest endpoint operation to (un)assign an action to a permission
	 * @param permissionId permission identifier
	 * @return OK (200): in case of successful operation.<br>
	 * Bad Request (404) and issue description: If the association could not be perform for not attending
	 * some business rules.
	 * Internal server error (500): In case of some error during processing
	 */
	public Response dissociate(long permissionId) {
		try {
			AssociationStatus associationStatus = businessService.dissociation(permissionId);
			if (associationStatus.isOK()) {
				return Response.ok().build();
			}
			return Response.status(Response.Status.BAD_REQUEST).entity(associationStatus.getMessage()).build();
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