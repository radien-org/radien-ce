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

import io.radien.api.entity.Page;
import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};
import io.radien.api.service.${entityResourceName.toLowerCase()}.${entityResourceName}ServiceAccess;

import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.${entityResourceName.toLowerCase()}.${entityResourceName}NotFoundException;

import ${package}.ms.client.entities.${entityResourceName};

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.io.Serializable;

import java.util.List;
/**
 * ${entityResourceName} Business Service will request the service
 * to validate the actions on the db
 *
 * @author Rajesh Gavvala
 */
@Stateless
public class ${entityResourceName}BusinessService implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(${entityResourceName}BusinessService.class);

	@Inject
	private ${entityResourceName}ServiceAccess ${entityResourceName.toLowerCase()}ServiceAccess;

	/**
	 * Gets all the ${entityResourceName.toLowerCase()} information into a paginated mode and return those information to the user.
	 * In case of omitted (empty) search parameter retrieves ALL ${entityResourceName.toLowerCase()}s
	 * @param search search parameter for matching ${entityResourceName.toLowerCase()}s (optional).
	 * @param pageNo page number where the user is seeing the information.
	 * @param pageSize number of ${entityResourceName.toLowerCase()}s to be showed in each page.
	 * @param sortBy Sorting fields
	 * @param isAscending Defines if ascending or descending in relation of sorting fields
	 */
	public Page<? extends System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		return ${entityResourceName.toLowerCase()}ServiceAccess.getAll(search, pageNo, pageSize, sortBy, isAscending);
	}


	/**
	 *  Gets the information of a ${entityResourceName.toLowerCase()} which will be found using the id.
	 *
	 * @param id to be searched for
	 * @return a paginated response with the requested information. 200 code message if success,
	 * 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
	 */
	public System${entityResourceName} getById(Long id) throws ${entityResourceName}NotFoundException {
		return ${entityResourceName.toLowerCase()}ServiceAccess.get(id);
	}

	/**
	 * Delete request which will delete the given id ${entityResourceName.toLowerCase()} information
	 *
	 * @param id record to be deleted
	 */
	public void delete(long id) throws ${entityResourceName}NotFoundException {
		${entityResourceName.toLowerCase()}ServiceAccess.delete(id);
	}

	/**
	 * Inserts the given ${entityResourceName.toLowerCase()} information, wither creates a new record or updated one already existent one, depending
	 * if the given ${entityResourceName.toLowerCase()} has an id or not.
	 *
	 * @param ${entityResourceName.toLowerCase()} information to be update or created.
	 */
	public void save(${entityResourceName} ${entityResourceName.toLowerCase()}) throws ${entityResourceName}NotFoundException, UniquenessConstraintException, ${entityResourceName}NotFoundException {
		${entityResourceName.toLowerCase()}ServiceAccess.save(${entityResourceName.toLowerCase()});
	}

	/**
	 * Will count how many ${entityResourceName.toLowerCase()}s are existent in the db
	 * @return a count of all the ${entityResourceName.toLowerCase()}s
	 */
	public long getTotalRecordsCount() {
		return ${entityResourceName.toLowerCase()}ServiceAccess.getTotalRecordsCount();
	}

}
