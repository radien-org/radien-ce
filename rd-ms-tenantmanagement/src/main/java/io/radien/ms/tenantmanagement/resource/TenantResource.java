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
package io.radien.ms.tenantmanagement.resource;

import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantSearchFilter;
import io.radien.ms.tenantmanagement.client.services.TenantResourceClient;
import io.radien.ms.tenantmanagement.service.TenantBusinessService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Tenant rest requests only regarding the tenants and his tables
 * @author Santana
 */
@RequestScoped
@Authenticated
public class TenantResource implements TenantResourceClient {

	@Inject
	private TenantBusinessService tenantBusinessService;


	/**
	 * Gets all the tenant information into a paginated mode and return those information to the user.
	 * @param search name description for some tenant
	 * @param pageNo of the requested information. Where the tenant is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.	 *
	 * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
	 * error.
	 */
	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		return Response.ok(tenantBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
	}

	/**
	 * Method to get all the requested tenant children tenants
	 * @param tenantId of the parent tenant
	 * @return a list of all the tenant children ids
	 */
	public Response getChildren(Long tenantId) {
		return Response.ok(tenantBusinessService.getChildren(tenantId)).build();
	}

	/**
	 * Gets a list of requested tenants based on some filtered information
	 * @param name to be searched for
	 * @param tenantType of the tenant to be searched
	 * @param ids list of ids to be search for
	 * @param isExact should the values be exact to the given ones
	 * @param isLogicalConjunction in case of true query will use an and in case of false query will use a or
	 * @return 200 response code in case of success or 500 in case of any issue
	 */
	@Override
	public Response get(String name, String tenantType, List<Long> ids, boolean isExact, boolean isLogicalConjunction) {
		SystemTenantSearchFilter filter = new TenantSearchFilter(name, tenantType, ids, isExact, isLogicalConjunction);
		return Response.ok(tenantBusinessService.getFiltered(filter)).build();
	}

	/**
	 * Gets tenant based on the given id
	 * @param id to be searched for
	 * @return 200 code message in case of success or 500 in case of any error
	 */
	@Override
	public Response getById(Long id) {
		return Response.ok(tenantBusinessService.get(id)).build();
	}

	/**
	 * Requests to a tenant be deleted by given his id
	 * @param id of the tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long id) {
		tenantBusinessService.delete(id);
		return Response.ok().build();
	}

	/**
	 * Request to delete a tenant and all his children
	 * @param id of the tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response deleteTenantHierarchy(long id) {
		tenantBusinessService.deleteTenantHierarchy(id);
		return Response.ok().build();
	}

	/**
	 * Method to request a creation of a tenant
	 * @param tenant information to be created
	 * @return a response with true or false based on the success or failure of the creation
	 */
	@Override
	public Response create(Tenant tenant) {
		tenantBusinessService.create(tenant);
		return Response.ok(tenant.getId()).build();
	}

	/**
	 * Method to update a requested tenant
	 * @param id of the tenant to be updated
 	 * @param tenant information to be update
	 * @return a response with true or false based on the success or failure of the update
	 */
	@Override
	public Response update(long id, Tenant tenant) {
		tenantBusinessService.update(id, tenant);
		return Response.ok().build();
	}

	/**
	 * Validates if specific requested Tenant exists
	 * @param id to be searched
	 * @return response 204 if tenant exists. 404 if do not exist.
	 * 500 in case of any other processing error.
	 */
	@Override
	public Response exists(Long id) {
		return tenantBusinessService.exists(id) ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
	}
}
