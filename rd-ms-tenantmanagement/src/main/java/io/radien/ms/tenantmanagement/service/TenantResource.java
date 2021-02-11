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
import io.radien.api.service.tenant.TenantServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
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
 *
 */
@RequestScoped
public class TenantResource implements TenantResourceClient {

	private static final Logger log = LoggerFactory.getLogger(TenantResource.class);
	@Inject
	private TenantServiceAccess tenantService;

	@Override
	public Response get(String name) {
		try {
			List<? extends SystemTenant> list= tenantService.get(name);
			return Response.ok(list).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}


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
		} catch (UniquenessConstraintException u){
			return getInvalidRequestResponse(u);
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
		}catch (UniquenessConstraintException u){
			return getInvalidRequestResponse(u);
		} catch (Exception e){
			return getGenericError(e);
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
	 * @param e exception to be throw
	 * @return code 400 message Generic Exception
	 */
	private Response getInvalidRequestResponse(UniquenessConstraintException e) {
		return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
	}

	/**
	 * Generic error exception to when the user could not be found in DB. Launches a 404 Error Code to the user.
	 * @return code 100 message Resource not found.
	 */
	private Response getResourceNotFoundException() {
		return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
	}

}
