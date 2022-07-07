/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.permissionmanagement.resource;

import io.radien.api.model.permission.SystemPermissionSearchFilter;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.entities.PermissionSearchFilter;
import io.radien.ms.permissionmanagement.client.services.PermissionResourceClient;

import io.radien.ms.permissionmanagement.service.PermissionBusinessService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Controller implementation responsible for deal with CRUD
 * operations requests (CRUD) regarding Permission domain object
 *
 * @author Newton Carvalho
 */
@RequestScoped
@Authenticated
public class PermissionResource implements PermissionResourceClient {

	@Inject
	private PermissionBusinessService permissionBusinessService;

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
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		return Response.ok(permissionBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
	}

	/**
	 * Finds all permissions that matches a search filter
	 * @param name permission name
	 * @param action action id
	 * @param resource resource id
	 * @param ids permission ids to be found
	 * @param isExact indicates if the match is for approximated value or not
	 * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
	 * @return In case of successful operation returns 200 (http status) and the collection (filled or not).
	 * Otherwise, in case of operational error, returns 500
	 */
	public Response getPermissions(String name, Long action, Long resource,
								   List<Long> ids, boolean isExact, boolean isLogicalConjunction) {
		SystemPermissionSearchFilter filter = new PermissionSearchFilter(name,action,resource,ids,isExact,isLogicalConjunction);
		return Response.ok(permissionBusinessService.getFiltered(filter)).build();
	}

	/**
	 * Retrieves an permission by its identifier
	 * @param id permission identifier
	 * @return If permission exists returns 200 status (and the correspondent object)
	 * Otherwise, if does not exist, return 404 status
	 * In case of operational error return 500 status
	 */
	public Response getById(Long id) {
		return Response.ok(permissionBusinessService.get(id)).build();
	}

	/**
	 * Retrieve the permission Id using the combination of resource and action as parameters
	 * @param resource resource name (Mandatory)
	 * @param action action name (Mandatory)
	 * @return Response OK (200) with the retrieved id (if exists). If not exist will return 404 status.
	 * In case of insufficient params (tenant or role not informed) It will return 400 status.
	 * For any other kind of (unpredictable) error this endpoint will return status 500
	 */
	public Response getIdByResourceAndAction(String resource, String action) {
		return Response.ok(permissionBusinessService.getIdByActionAndResource(resource, action)).build();
	}


	/**
	 * Deletes an permission by its identifier
	 * @param id permission identifier
	 * @return Returns 200 status, Otherwise, in case of operational error return 500 status
	 */
	public Response delete(long id) {
		permissionBusinessService.delete(id);
		return Response.ok().build();
	}

	/**
	 * Creates a permission
	 * @param permission permission to be created
	 * @return Http status 200 in case of successful operation.
	 * Bad request (400) in case of trying to create an permission with repeated description.
	 * Internal Server Error (500) in case of operational error
	 */
	public Response create(Permission permission) {
		permissionBusinessService.create(permission);
		return Response.ok().build();
	}

	/**
	 * Updates a permission
	 * @param permission permission to be updated
	 * @return Http status 200 in case of successful operation.
	 * Bad request (400) in case of trying to create an permission with repeated description.
	 * Not found (404) in case of not existent Permission for the given id.
	 * Internal Server Error (500) in case of operational error
	 */
	public Response update(long id, Permission permission) {
		permissionBusinessService.update(id, permission);
		return Response.ok().build();
	}

	/**
	 * Validates if Permission exists for a referred Id (or alternatively taking in account name)
	 * @param id Identifier to guide the search be searched (Primary parameter)
	 * @param name Permission name, an alternative parameter to be used (only if Id is omitted)
	 * @return 200: If Permission exists
	 *     	   404: If Permission does not exist
	 *         400: (Bad Request): None expected parameter informed
	 *         500: In case of any other issue
	 */
	@Override
	public Response exists(Long id, String name) {
		if (permissionBusinessService.exists(id, name))
			return Response.noContent().build();

		return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
	}

	/**
	 * Will calculate how many records are existent in the db
	 * @return the count of existent permissions.
	 */
	@Override
	public Response getTotalRecordsCount() {
		return Response.ok(permissionBusinessService.getCount()).build();
	}
}