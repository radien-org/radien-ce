/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.NotFoundException;
import io.radien.exception.TenantException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantSearchFilter;
import io.radien.ms.tenantmanagement.client.services.TenantResourceClient;
import io.radien.ms.tenantmanagement.entities.TenantEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Tenant rest requests only regarding the tenants and his tables
 * @author Santana
 */
@RequestScoped
public class TenantResource implements TenantResourceClient {

	private static final Logger log = LoggerFactory.getLogger(TenantResource.class);
	@Inject
	private TenantServiceAccess tenantService;


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
		try {
			log.info("Will get all the role information I can find!");
			return Response.ok(tenantService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch(Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
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
		try {
			SystemTenantSearchFilter filter = new TenantSearchFilter(name, tenantType, ids, isExact, isLogicalConjunction);
			List<? extends SystemTenant> list= tenantService.get(filter);
			return Response.ok(list).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Gets tenant based on the given id
	 * @param id to be searched for
	 * @return 200 code message in case of success or 500 in case of any error
	 */
	@Override
	public Response getById(Long id) {
		try {
			SystemTenant tenant = tenantService.get(id);
			if(tenant == null){
				return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
			}
			return Response.ok(tenant).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Requests to a tenant be deleted by given his id
	 * @param id of the tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long id) {
		try {
			return Response.ok(tenantService.delete(id)).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Request to delete a tenant and all his children
	 * @param id of the tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response deleteTenantHierarchy(long id) {
		try {
			return Response.ok(tenantService.deleteTenantHierarchy(id)).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Method to request a creation of a tenant
	 * @param tenant information to be created
	 * @return a response with true or false based on the success or failure of the creation
	 */
	@Override
	public Response create(Tenant tenant) {
		try {
            tenantService.create(new TenantEntity(tenant));
			return Response.ok(tenant.getId()).build();
		} catch (TenantException | UniquenessConstraintException u){
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Method to update a requested tenant
	 * @param id of the tenant to be updated
 	 * @param tenant information to be update
	 * @return a response with true or false based on the success or failure of the update
	 */
	@Override
	public Response update(long id, Tenant tenant) {
		try {
			tenant.setId(id);
            tenantService.update(new TenantEntity(tenant));
			return Response.ok().build();
		}catch (TenantException | UniquenessConstraintException u){
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Validates if specific requested Tenant exists
	 * @param id to be searched
	 * @return response 204 if tenant exists. 404 if do not exist.
	 * 500 in case of any other processing error.
	 */
	@Override
	public Response exists(Long id) {
		try {
			return tenantService.exists(id) ? Response.noContent().build() :
					Response.status(Response.Status.NOT_FOUND).build();
		}
		catch (Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}
}
