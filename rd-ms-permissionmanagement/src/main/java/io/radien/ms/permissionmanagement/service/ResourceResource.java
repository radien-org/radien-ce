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

import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.permissionmanagement.client.entities.ResourceSearchFilter;
import io.radien.ms.permissionmanagement.client.services.ResourceResourceClient;
import io.radien.ms.permissionmanagement.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ResourceResource implements ResourceResourceClient {

	private Logger log = LoggerFactory.getLogger(ResourceResource.class);

	@Inject
	private ResourceServiceAccess resourceServiceAccess;

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
	public Response getAll(String search,
						   int pageNo,
						   int pageSize,
						   List<String> sortBy,
						   boolean isAscending) {
		try {
			return Response.ok(resourceServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Finds all resources that matches a name
	 * @param name resource name
	 * @param isExact indicates if the match is for approximated value or not
	 * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
	 * @return In case of successful operation returns 200 (http status)
	 * and the collection (filled or not).
	 * Otherwise, in case of operational error, returns 500
	 */
	public Response getResources(String name, boolean isExact,
							   boolean isLogicalConjunction) {

		try {
			return Response.ok(resourceServiceAccess.getResources(new ResourceSearchFilter(name,
					isExact, isLogicalConjunction))).build();
		} catch (Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Retrieves an resource by its identifier
	 * @param id resource identifier
	 * @return If resource exists returns 200 status (and the correspondent object)
	 * Otherwise, if does not exist, return 404 status
	 * In case of operational error return 500 status
	 */
	public Response getById(Long id) {
		try {
			SystemResource systemResource = resourceServiceAccess.get(id);
			if(systemResource == null){
				return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
			}
			return Response.ok(systemResource).build();
		} catch(Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Deletes an resource by its identifier
	 * @param id resource identifier
	 * @return Returns 200 status, Otherwise, in case of operational error return 500 status
	 */
	public Response delete(long id) {
		try {
			resourceServiceAccess.delete(id);
		} catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
		return Response.ok().build();
	}

	/**
	 * Saves an resource (Creation or Update).
	 * @param resource resource to be created or update
	 * @return Http status 200 in case of successful operation.<br>
	 * Bad request (404) in case of trying to create an resource with repeated description.<br>
	 * Internal Server Error (500) in case of operational error
	 */
	public Response save(io.radien.ms.permissionmanagement.client.entities.Resource resource) {
		try {
			resourceServiceAccess.save(new Resource(resource));
			return Response.ok().build();
		} catch (UniquenessConstraintException e) {
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
		} catch (Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}
}