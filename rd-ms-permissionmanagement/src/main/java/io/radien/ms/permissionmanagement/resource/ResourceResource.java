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

import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.entities.ResourceSearchFilter;
import io.radien.ms.permissionmanagement.client.services.ResourceResourceClient;

import io.radien.ms.permissionmanagement.service.ResourceBusinessService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Controller implementation responsible for deal with CRUD
 * operations requests (CRUD) regarding Resource domain object
 *
 * @author Newton Carvalho
 */
@Path("resource")
@RequestScoped
@Authenticated
public class ResourceResource implements ResourceResourceClient {

	@Inject
	private ResourceBusinessService resourceBusinessService;

	/**
	 * Retrieves a page object containing resources that matches search parameter.
	 * In case of omitted (empty) search parameter retrieves ALL resources
	 * @param search search parameter for matching resources (optional).
	 * @param pageNo page number
	 * @param pageSize page size
	 * @param sortBy Sorting fields
	 * @param isAscending Defines if ascending or descending in relation of sorting fields
	 * @return In case of successful operation returns OK (http status 200)
	 * and the page object (filled or not).
	 * Otherwise, in case of operational error, returns Internal Server Error (500)
	 */
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		return Response.ok(resourceBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
	}

	/**
	 * Finds all resources that matches a name
	 * @param name resource name
	 * @param ids resource ids to be found
	 * @param isExact indicates if the match is for approximated value or not
	 * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
	 * @return In case of successful operation returns 200 (http status)
	 * and the collection (filled or not).
	 * Otherwise, in case of operational error, returns 500
	 */
	public Response getResources(String name, List<Long> ids, boolean isExact, boolean isLogicalConjunction) {
		return Response.ok(resourceBusinessService.getFiltered(new ResourceSearchFilter(name, ids, isExact, isLogicalConjunction))).build();
	}

	/**
	 * Retrieves an resource by its identifier
	 * @param id resource identifier
	 * @return If resource exists returns 200 status (and the correspondent object)
	 * Otherwise, if does not exist, return 404 status
	 * In case of operational error return 500 status
	 */
	public Response getById(Long id) {
		return Response.ok(resourceBusinessService.get(id)).build();
	}

	/**
	 * Deletes an resource by its identifier
	 * @param id resource identifier
	 * @return Returns 200 status, Otherwise, in case of operational error return 500 status
	 */
	public Response delete(long id) {
		resourceBusinessService.delete(id);
		return Response.ok().build();
	}

	/**
	 * Creates a resource
	 * @param resource resource to be created
	 * @return Http status 200 in case of successful operation.
	 * Bad request (400) in case of trying to create a resource with repeated description.
	 * Internal Server Error (500) in case of operational error
	 */
	public Response create(Resource resource) {
		resourceBusinessService.create(resource);
		return Response.ok().build();
	}

	/**
	 * Updates a resource
	 * @param id resource identifier
	 * @param resource resource to be updated
	 * @return Http status 200 in case of successful operation.
	 * Bad request (400) in case of trying to update a resource with repeated description.
	 * Not found (404) in case of trying to update a resource that does not exist for a given id.
	 * Internal Server Error (500) in case of operational error
	 */
	public Response update(long id, Resource resource) {
		resourceBusinessService.update(id, resource);
		return Response.ok().build();
	}
}