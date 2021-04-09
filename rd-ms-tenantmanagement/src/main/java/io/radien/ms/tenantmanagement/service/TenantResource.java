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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantSearchFilter;
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.NotFoundException;
import io.radien.exception.TenantException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantSearchFilter;
import io.radien.ms.tenantmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.tenantmanagement.client.services.TenantResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Santana
 */
@RequestScoped
public class TenantResource implements TenantResourceClient {

	private static final Logger log = LoggerFactory.getLogger(TenantResource.class);
	@Inject
	private TenantServiceAccess tenantService;


	/**
	 * Gets all the tenant information into a paginated mode and return those information to the user.
	 * @param pageNo page number where the user is seeing the information.
	 * @param pageSize number of tenants to be showed in each page.
	 * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
	 * error.
	 */
	@Override
	public Response getAll(int pageNo, int pageSize) {
		try {
			log.info("Will get all the role information I can find!");
			return Response.ok(tenantService.getAll(pageNo, pageSize)).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Gets a list of requested tenants based on some filtered information
	 * @param name to be searched for
	 * @param type of the tenant to be searched
	 * @param isExact should the values be exact to the given ones
	 * @param isLogicalConjunction in case of true query will use an and in case of false query will use a or
	 * @return 200 response code in case of success or 500 in case of any issue
	 */
	@Override
	public Response get(String name, String type, boolean isExact, boolean isLogicalConjunction) {
		try {
			SystemTenantSearchFilter filter = new TenantSearchFilter(name, type, isExact, isLogicalConjunction);
			List<? extends SystemTenant> list= tenantService.get(filter);
			return Response.ok(list).build();
		}catch (Exception e){
			return getGenericError(e);
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
				return getResourceNotFoundException();
			}
			return Response.ok(tenant).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	@Override
	public Response delete(long id) {
		try {
			return Response.ok(tenantService.delete(id)).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	@Override
	public Response create(Tenant tenant) {
		try {
            tenantService.create(new io.radien.ms.tenantmanagement.entities.Tenant(tenant));
			return Response.ok(tenant.getId()).build();
		} catch (TenantException | UniquenessConstraintException u){
			return getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	@Override
	public Response update(long id, Tenant tenant) {
		try {
			tenant.setId(id);
            tenantService.update(new io.radien.ms.tenantmanagement.entities.Tenant(tenant));
			return Response.ok().build();
		}catch (TenantException | UniquenessConstraintException u){
			return getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Validates if specific requested Tenant exists
	 * @param id to be searched
	 * @return response true if it exists
	 */
	@Override
	public Response exists(Long id) {
		try {
			return Response.ok(tenantService.exists(id)).build();
		}catch (NotFoundException e){
			return getResourceNotFoundException();
		}
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
	 * Invalid Request error exception. Launches a 400 Error Code to the user.
	 * @param errorMessage exception message to be throw
	 * @return code 400 message Generic Exception
	 */
	private Response getInvalidRequestResponse(String errorMessage) {
		return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
	}


	/**
	 * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
	 * @return code 100 message Resource not found.
	 */
	private Response getResourceNotFoundException() {
		return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
	}

}
