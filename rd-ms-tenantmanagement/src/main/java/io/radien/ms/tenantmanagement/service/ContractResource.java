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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.ms.tenantmanagement.client.services.ContractResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Contract requests only regarding the contracts and his tables
 * @author Nuno Santana
 */
@RequestScoped
public class ContractResource implements ContractResourceClient {

	private static final Logger log = LoggerFactory.getLogger(ContractResource.class);

	@Inject
	private ContractServiceAccess contractService;

	/**
	 * Gets all the contract information into a paginated mode and return those information to the user.
	 * @param pageNo page number where the user is seeing the information.
	 * @param pageSize number of contract to be showed in each page.
	 * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
	 * error.
	 */
	@Override
	public Response getAll(int pageNo, int pageSize) {
		try {
			log.info("Will get all the role information I can find!");
			return Response.ok(contractService.getAll(pageNo, pageSize)).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Will calculate how many records are existent in the db
	 * @return the count of existent contracts.
	 */
	@Override
	public Response getTotalRecordsCount() {
		try {
			return Response.ok(contractService.getTotalRecordsCount()).build();
		} catch(Exception e) {
			return getGenericError(e);
		}
	}

	/**
	 * Method to retrieve a specific contract based on the given name
	 * @param name to be search
	 * @return the requested contract
	 */
	@Override
	public Response get(String name) {
		try {
			List<? extends SystemContract> list= contractService.get(name);
			return Response.ok(list).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Method to get a specific contract based on the requested id
	 * @param id to be search
	 * @return the requested contract
	 */
	@Override
	public Response getById(Long id) {
		try {
			SystemContract contract = contractService.get(id);
			if(contract == null){
				return getResourceNotFoundException();
			}
			return Response.ok(contract).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Method to delete a specific contract based on his id
	 * @param id of the contract to be deleted
	 * @return a response with true or false based on the success or failure of the deletion
	 */
	@Override
	public Response delete(long id) {
		try {
			return Response.ok(contractService.delete(id)).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Request to create a specific and given contract object
	 * @param contract object information to be created
	 * @return a response with true or false based on the success or failure of the creation
	 */
	@Override
	public Response create(Contract contract) {
		try {
            contractService.create(new io.radien.ms.tenantmanagement.entities.Contract(contract));
			return Response.ok(contract.getId()).build();
		}catch (UniquenessConstraintException u){
			return getInvalidRequestResponse(u);
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Request to update a specific contract
	 * @param id of the contract to be update
	 * @param contract information to update
	 * @return a response with true or false based on the success or failure of the update
	 */
	@Override
	public Response update(long id, Contract contract) {
		try {
			contract.setId(id);
            contractService.update(new io.radien.ms.tenantmanagement.entities.Contract(contract));
			return Response.ok().build();
		}catch (UniquenessConstraintException u){
			return getInvalidRequestResponse(u);
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	/**
	 * Validates if specific requested Contract exists
	 * @param id to be searched
	 * @return response true if it exists
	 */
	@Override
	public Response exists(Long id) {
		try {
			return Response.ok(contractService.exists(id)).build();
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
