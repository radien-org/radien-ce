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
package io.radien.ms.tenantmanagement.service;

import io.radien.api.model.ModelValueId;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemActiveTenantSearchFilter;
import io.radien.api.service.tenant.ActiveTenantServiceAccess;
import io.radien.exception.ActiveTenantException;
import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenantSearchFilter;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantResourceClient;
import io.radien.ms.tenantmanagement.entities.ActiveTenantEntity;
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
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Gets a list of requested active tenants based on some filtered information
	 * @param userId to be searched for
	 * @param tenantId to be search for
	 * @param tenantName to be search for
	 * @param isTenantActive to be search for
	 * @param isLogicalConjunction in case of true query will use an and in case of false query will use a or
	 * @return 200 response code in case of success or 500 in case of any issue
	 */
	@Override
	public Response get(Long userId, Long tenantId, boolean isLogicalConjunction) {
		try {
			SystemActiveTenantSearchFilter filter = new ActiveTenantSearchFilter(userId, tenantId, isLogicalConjunction);
			List<? extends SystemActiveTenant> list= activeTenantServiceAccess.get(filter);
			return Response.ok(list).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
				return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
			}
			return Response.ok(activeTenant).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Gets active tenant based on the given id
	 * @param userId to be searched for
	 * @param tenantId to be searched for
	 * @return 200 code message in case of success or 500 in case of any error
	 */
	@Override
	public Response getByUserAndTenant(Long userId, Long tenantId) {
		try {
			List<? extends SystemActiveTenant> list= activeTenantServiceAccess.getByUserAndTenant(userId, tenantId);
			return Response.ok(list).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Requests to delete active tenants taking in account the following parameters
	 * @param tenantId tenant id of the active tenant to be deleted
	 * @param userId user id of the active tenant to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long tenantId, long userId) {
		try {
			return Response.ok(activeTenantServiceAccess.delete(tenantId, userId)).build();
		}catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
			ActiveTenantEntity activeTenantEntity = new ActiveTenantEntity(activeTenant);
            activeTenantServiceAccess.create(activeTenantEntity);
			ModelValueId modelValueId = new ModelValueId(); modelValueId.setId(activeTenantEntity.getId());
			return Response.ok(modelValueId).build();
		} catch (ActiveTenantException | UniquenessConstraintException u){
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
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
            activeTenantServiceAccess.update(new ActiveTenantEntity(activeTenant));
			return Response.ok().build();
		}catch (ActiveTenantException | UniquenessConstraintException u){
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(u.getMessage());
		} catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Validates if specific requested active Tenant exists
	 * @param userId to be found
	 * @param tenantId to be found
	 * @return response true if it exists
	 */
	@Override
	public Response exists(Long userId, Long tenantId) {
		try {
			return Response.ok(activeTenantServiceAccess.exists(userId, tenantId)).build();
		}catch (NotFoundException e){
			return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
		}
	}
}
