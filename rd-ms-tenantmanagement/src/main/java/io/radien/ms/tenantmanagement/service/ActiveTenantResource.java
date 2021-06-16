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

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.exception.ActiveTenantException;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Active Tenant rest requests only regarding the associations between the tenants and the users in his tables
 * @author Bruno Gama
 */
@RequestScoped
public class ActiveTenantResource implements ActiveTenantResourceClient {

	private static final Logger log = LoggerFactory.getLogger(ActiveTenantResource.class);

	@Inject
	private ActiveTenantServiceAccess activeTenantServiceAccess;

	/**
	 * Gets all the active tenant information into a paginated mode and return those information to the user.
	 * @param search name description for some active tenant
	 * @param pageNo of the requested information. Where the active tenant is.
	 * @param pageSize total number of pages returned in the request.
	 * @param sortBy sort filter criteria.
	 * @param isAscending ascending filter criteria.
	 * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
	 * error.
	 */
	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		try {
			log.info("Will get all the active tenant information I can find!");
			return Response.ok(activeTenantServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Gets active tenant based on the given id
	 * @param id to be searched for
	 * @return 200 code message in case of success or 500 in case of any error
	 */
	@Override
	public Response getById(Long id) {
		try {
			SystemActiveTenant activeTenant = activeTenantServiceAccess.get(id);
			if(activeTenant == null){
				return getResourceNotFoundException();
			}
			return Response.ok(activeTenant).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Requests to a active tenant be deleted by given his id
	 * @param id of the active tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long id) {
		try {
			return Response.ok(activeTenantServiceAccess.delete(id)).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Method to request a creation of a active tenant
	 * @param activeTenant information to be created
	 * @return a response with true or false based on the success or failure of the creation
	 */
	@Override
	public Response create(ActiveTenant activeTenant) {
		try {
            activeTenantServiceAccess.create(new io.radien.ms.tenantmanagement.entities.ActiveTenant(activeTenant));
			return Response.ok(activeTenant.getId()).build();
		} catch (ActiveTenantException | UniquenessConstraintException u){
			return getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Method to update a requested active tenant
	 * @param id of the active tenant to be updated
 	 * @param activeTenant information to be update
	 * @return a response with true or false based on the success or failure of the update
	 */
	@Override
	public Response update(long id, ActiveTenant activeTenant) {
		try {
			activeTenant.setId(id);
            activeTenantServiceAccess.update(new io.radien.ms.tenantmanagement.entities.ActiveTenant(activeTenant));
			return Response.ok().build();
		}catch (ActiveTenantException | UniquenessConstraintException u){
			return getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Validates if specific requested active Tenant exists
	 * @param id to be searched
	 * @return response true if it exists
	 */
	@Override
	public Response exists(Long id) {
		try {
			return Response.ok(activeTenantServiceAccess.exists(id)).build();
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
