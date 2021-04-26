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
package ${package}.ms.service.services;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import ${package}.api.model.System${entityResourceName};
import ${package}.exception.${entityResourceName}NotFoundException;
import ${package}.ms.client.exceptions.ErrorCodeMessage;
import ${package}.ms.client.exceptions.RemoteResourceException;
import ${package}.ms.client.services.${entityResourceName}ResourceClient;
import ${package}.ms.service.entities.${entityResourceName};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author Nuno Santana
 * @author Bruno Gama
 *
 */
@Path("${entityResourceName.toLowerCase()}")
@RequestScoped
public class ${entityResourceName}Resource implements ${entityResourceName}ResourceClient {

	private static final long serialVersionUID = 6812608123262000068L;

	@Inject
	private ${entityResourceName}BusinessService ${entityResourceName.toLowerCase()}BusinessService;

	private static final Logger log = LoggerFactory.getLogger(${entityResourceName}Resource.class);

	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		try {
			return Response.ok(${entityResourceName.toLowerCase()}BusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch (Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Returns JSON message with the specific required information search by the ${entityResourceName.toLowerCase()} ID.
	 * @param id to be search
	 * @return Ok message if it has success. Returns error 404 Code to the ${entityResourceName.toLowerCase()} in case of resource is not existent.
	 */
	public Response getById(Long id) {
		try {
			System${entityResourceName} system${entityResourceName} = ${entityResourceName.toLowerCase()}BusinessService.get(id);
			return Response.ok(system${entityResourceName}).build();
		} catch(Exception e) {
			return getResponseFromException(e);
		}
	}

	/**
	 * Deletes requested ${entityResourceName.toLowerCase()} from the DB
	 * @param id of the ${entityResourceName.toLowerCase()} to be deleted
	 * @return error 404 Code to the ${entityResourceName.toLowerCase()} in case of resource is not existent.
	 */
	public Response delete(long id)  {
		try {
			${entityResourceName.toLowerCase()}BusinessService.delete(id);
		}  catch (Exception e){
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	@Override
	public Response save(io.radien.ms.client.entities.${entityResourceName} ${entityResourceName.toLowerCase()}) {
		try {
			${entityResourceName.toLowerCase()}BusinessService.save(new ${entityResourceName}(${entityResourceName.toLowerCase()}));
		} catch (Exception e) {
			return getResponseFromException(e);
		}
		return Response.ok().build();
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the ${entityResourceName.toLowerCase()}.
	 * @param e exception to be throw
	 * @return code 500 message Generic Exception
	 */
	private Response getGenericError(Exception e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
	}

	/**
	 * Generic error exception. Launches a 500 Error Code to the ${entityResourceName.toLowerCase()}.
	 * @param e exception to be throw
	 * @return code 500 message Generic Exception
	 */
	private Response getRemoteResourceExceptionError(RemoteResourceException e) {
		String message = ErrorCodeMessage.GENERIC_ERROR.toString();
		log.error(message, e);
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}

	/**
	 * Generic error exception to when the ${entityResourceName.toLowerCase()} could not be found in DB. Launches a 404 Error Code to the ${entityResourceName.toLowerCase()}.
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
		} catch (${entityResourceName}NotFoundException unfe){
			response = getResourceNotFoundException();
		}catch (Exception et){
			response = getGenericError(et);
		}
		return response;
	}
}
