/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};

import io.radien.exception.GenericErrorMessagesToResponseMapper;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.${entityResourceName.toLowerCase()}.${entityResourceName}NotFoundException;

import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.services.${entityResourceName}ResourceClient;
import ${package}.ms.service.entities.${entityResourceName}Entity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * ${entityResourceName} Business Service will request the service
 * to validate the actions on the db
 *
 * @author Rajesh Gavvala
 */
@Path("${entityResourceName.toLowerCase()}")
@RequestScoped
public class ${entityResourceName}Resource implements ${entityResourceName}ResourceClient {
	private static final Logger log = LoggerFactory.getLogger(${entityResourceName}Resource.class);

	@Inject
	private ${entityResourceName}BusinessService ${entityResourceName.toLowerCase()}BusinessService;


	/**
	 * Retrieves a page object containing ${entityResourceName.toLowerCase()}s that matches search parameter.
	 * In case of omitted (empty) search parameter retrieves ALL ${entityResourceName.toLowerCase()}s
	 * @param search search parameter for matching ${entityResourceName.toLowerCase()}s (optional).
	 * @param pageNo page number where the user is seeing the information.
	 * @param pageSize number of ${entityResourceName.toLowerCase()}s to be showed in each page.
	 * @param sortBy Sorting fields
	 * @param isAscending Defines if ascending or descending in relation of sorting fields
	 * @return In case of successful operation returns OK (http status 200)
	 * and the page object (filled or not).<br>
	 * Otherwise, in case of operational error, returns Internal Server Error (500)
	 */
	@Override
	public Response getAll(String search,
						   int pageNo,
						   int pageSize,
						   List<String> sortBy,
						   boolean isAscending) {
		try {
			log.info("Will get all the ${entityResourceName.toLowerCase()} information I can find!");
			return Response.ok(${entityResourceName.toLowerCase()}BusinessService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
		} catch(Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 *  Gets the information of a ${entityResourceName.toLowerCase()} which will be found using the id.
	 *
	 * @param id to be searched for
	 * @return a paginated response with the requested information. 200 code message if success,
	 * 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
	 */
	@Override
	public Response getById(Long id) {
		try{
			log.info("Will search for a specific ${entityResourceName.toLowerCase()} with the following id {}.", id);
			System${entityResourceName} system${entityResourceName} = ${entityResourceName.toLowerCase()}BusinessService.getById(id);
			return Response.ok(system${entityResourceName}).build();
		} catch (${entityResourceName}NotFoundException e) {
			return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
		} catch (Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Delete request which will delete the given id ${entityResourceName.toLowerCase()} information
	 *
	 * @param id record to be deleted
	 * @return 200 code message if success, 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
	 */
	@Override
	public Response delete(long id) {
		try {
			log.info("Will delete a specific ${entityResourceName.toLowerCase()} with the following id {}.", id);
			${entityResourceName.toLowerCase()}BusinessService.getById(id);
			${entityResourceName.toLowerCase()}BusinessService.delete(id);
		} catch (${entityResourceName}NotFoundException e) {
			return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
		} catch (Exception e){
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
		return Response.ok().build();
	}

	/**
	 * Inserts the given ${entityResourceName.toLowerCase()} information, wither creates a new record or updated one already existent one, depending
	 * if the given ${entityResourceName.toLowerCase()} has an id or not.
	 *
	 * @param ${entityResourceName.toLowerCase()} information to be update or created.
	 * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
	 * 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
	 */
	@Override
	public Response save(${entityResourceName} ${entityResourceName.toLowerCase()}) {
		try {
			log.info("New information to be created/updated it's on it's way!");
			${entityResourceName.toLowerCase()}BusinessService.save(new ${entityResourceName}Entity(${entityResourceName.toLowerCase()}));
			return Response.ok().build();
		} catch (${entityResourceName}NotFoundException e) {
			return GenericErrorMessagesToResponseMapper.getResourceNotFoundException();
		} catch (UniquenessConstraintException e) {
			return GenericErrorMessagesToResponseMapper.getInvalidRequestResponse(e.getMessage());
		} catch (Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}

	/**
	 * Will calculate how many records are existent in the db
	 * @return the count of existent ${entityResourceName.toLowerCase()}s.
	 */
	@Override
	public Response getTotalRecordsCount() {
		try {
			return Response.ok(${entityResourceName.toLowerCase()}BusinessService.getTotalRecordsCount()).build();
		} catch(Exception e) {
			return GenericErrorMessagesToResponseMapper.getGenericError(e);
		}
	}
}
