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

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantResourceClient;
import io.radien.ms.tenantmanagement.service.ActiveTenantBusinessService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Active Tenant rest requests only regarding the associations between the tenants and the users in his tables
 * @author Bruno Gama
 */
@RequestScoped
@Authenticated
public class ActiveTenantResource implements ActiveTenantResourceClient {

	@Inject
	private ActiveTenantBusinessService activeTenantBusinessService;

	/**
	 * Gets all the active tenant information into a paginated mode and return those information to the user.
	 * @param tenantId tenant identifier (Optional)
	 * @param userId user identifier (Optional)
	 * @param pageNo of the requested information. Where the active tenant is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
	 * error.
	 */
	@Override
	public Response getAll(Long tenantId, Long userId, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		return Response.ok(activeTenantBusinessService.getAll(tenantId, userId, pageNo, pageSize, sortBy, isAscending)).build();
	}

	/**
	 * Gets a list of requested active tenants based on some filtered information
	 * @param userId to be searched for
	 * @param tenantId to be search for
	 * @param isLogicalConjunction in case of true query will use an and in case of false query will use a or
	 * @return 200 response code in case of success or 500 in case of any issue
	 */
	@Override
	public Response get(Long userId, Long tenantId, boolean isLogicalConjunction) {
		SystemActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(userId, tenantId, isLogicalConjunction);
		List<? extends SystemActiveTenant> list= activeTenantBusinessService.getFiltered(filter);
		return Response.ok(list).build();
	}

	/**
	 * Gets active tenant based on the given id
	 * @param id to be searched for
	 * @return 200 code message in case of success or 500 in case of any error
	 */
	@Override
	public Response getById(Long id) {
		SystemActiveTenant activeTenant = activeTenantBusinessService.get(id);
		return Response.ok(activeTenant).build();
	}


	/**
	 * Requests to a active tenant be deleted by given his id
	 * @param id of the active tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long id) {
		activeTenantBusinessService.delete(id);
		return Response.ok().build();
	}

	/**
	 * Requests to delete active tenants taking in account the following parameters
	 * @param tenantId tenant id of the active tenant to be deleted
	 * @param userId user id of the active tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long tenantId, long userId) {
		activeTenantBusinessService.delete(tenantId, userId);
		return Response.ok().build();
	}

	/**
	 * Method to request a creation of a active tenant
	 * @param activeTenant information to be created
	 * @return a response with true or false based on the success or failure of the creation
	 */
	@Override
	public Response create(ActiveTenant activeTenant) {
		return Response.ok(activeTenantBusinessService.create(activeTenant)).build();
	}

	/**
	 * Method to update a requested active tenant
	 * @param id of the active tenant to be updated
 	 * @param activeTenant information to be update
	 * @return a response with true or false based on the success or failure of the update
	 */
	@Override
	public Response update(long id, ActiveTenant activeTenant) {
		activeTenantBusinessService.update(id, activeTenant);
		return Response.ok().build();
	}

	/**
	 * Validates if specific requested active Tenant exists
	 * @param userId to be found
	 * @param tenantId to be found
	 * @return response true if it exists
	 */
	@Override
	public Response exists(Long userId, Long tenantId) {
		return Response.ok(activeTenantBusinessService.exists(userId, tenantId)).build();
	}
}
