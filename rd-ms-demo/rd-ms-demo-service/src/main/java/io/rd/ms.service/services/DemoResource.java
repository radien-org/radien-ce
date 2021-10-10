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
package io.rd.ms.service.services;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.rd.api.model.SystemDemo;
import io.rd.exception.DemoNotFoundException;
import io.rd.ms.client.exceptions.ErrorCodeMessage;
import io.rd.ms.client.exceptions.RemoteResourceException;
import io.rd.ms.client.services.DemoResourceClient;
import io.rd.ms.service.entities.Demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author Nuno Santana
 * @author Bruno Gama
 *
 */
@Path("demo")
@RequestScoped
public class DemoResource implements DemoResourceClient {

	private static final long serialVersionUID = 6812608123262000068L;

	@Inject
	private DemoBusinessService demoBusinessService;

	private static final Logger log = LoggerFactory.getLogger(DemoResource.class);

	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		try {
			return Response.ok(demoBusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Returns JSON message with the specific required information search by the demo ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the demo in case of resource is not existent.
	 */
	public Response getById(Long id) {
		try {
			SystemDemo systemDemo = demoBusinessService.get(id);
			return Response.ok(systemDemo).build();
		} catch(Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Deletes requested demo from the DB
	 * @param id of the demo to be deleted
	 * @return error 404 Code to the demo in case of resource is not existent.
	 */
	public Response delete(long id)  {
		try {
			demoBusinessService.delete(id);
		}  catch (Exception e){
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	@Override
	public Response save(io.rd.ms.client.entities.Demo demo) {
		try {
			demoBusinessService.save(new Demo(demo));
		} catch (Exception e) {
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the demo.
	 * @param e exception to be throw
	 * @return code 500 message Generic Exception
	 */
	private Response getGenericError(Exception e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the demo.
	 * @param e exception to be throw
	 * @return code 500 message Generic Exception
	 */
	private Response getRemoteResourceExceptionError(RemoteResourceException e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}

	/**
	 * Generic error exception to when the demo could not be found in DB. Launches a 404 Error Code to the demo.
	 * @return code 100 message Resource not found.
	 */
	private Response getResourceNotFoundException() {
		return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
	}

	private Response getResponseFromException(Exception e){
		Response response;
		try{
			log.error("ERROR: ",e);
			throw e;
		} catch (RemoteResourceException rre){
			response = getRemoteResourceExceptionError(rre);
		} catch (DemoNotFoundException unfe){
			response = getResourceNotFoundException();
		}catch (Exception et){
			response = getGenericError(et);
		}
		return response;
	}
}
