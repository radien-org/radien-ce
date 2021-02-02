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
package io.radien.ms.contractmanagement.service;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import io.radien.api.model.contract.SystemContract;
import io.radien.api.service.contract.ContractServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.contractmanagement.client.entities.Contract;
import io.radien.ms.contractmanagement.client.exceptions.ErrorCodeMessage;
import io.radien.ms.contractmanagement.client.services.ContractResourceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Santana
 *
 */
@RequestScoped
public class ContractResource implements ContractResourceClient {

	private static final Logger log = LoggerFactory.getLogger(ContractResource.class);
	@Inject
	private ContractServiceAccess contractService;

	@Override
	public Response get(String name) {
		try {
			List<? extends SystemContract> list= contractService.get(name);
			return Response.ok(list).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}


	@Override
	public Response getById(Long id) {
		try {
			return Response.ok(contractService.get(id)).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	@Override
	public Response delete(long id) {
		try {
			return Response.ok(contractService.delete(id)).build();
		}catch (Exception e){
			return getGenericError(e);
		}
	}

	@Override
	public Response create(Contract contract) {
		try {
            contractService.create(new io.radien.ms.contractmanagement.entities.Contract(contract));
			return Response.ok(contract.getId()).build();
		}catch (UniquenessConstraintException u){
			return getInvalidRequestResponse(u);
		} catch (Exception e){
			return getGenericError(e);
		}
	}

	@Override
	public Response update(long id, Contract contract) {
		try {
			contract.setId(id);
            contractService.update(new io.radien.ms.contractmanagement.entities.Contract(contract));
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
}
