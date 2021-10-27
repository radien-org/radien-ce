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
package ${package}.ms.service.services;


import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}ServiceAccess;
import ${package}.exception.${entityResourceName}NotFoundException;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.exceptions.RemoteResourceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.io.Serializable;

import java.util.List;


/**
 * @author Nuno Santana
 */

@Stateless
public class ${entityResourceName}BusinessService implements Serializable {

	private static final long serialVersionUID = 6812608123262000067L;

	private static final Logger log = LoggerFactory.getLogger(${entityResourceName}BusinessService.class);


	@Inject
	private ${entityResourceName}ServiceAccess ${entityResourceName.toLowerCase()}ServiceAccess;

	public void save(${entityResourceName} ${entityResourceName.toLowerCase()}) throws ${entityResourceName}NotFoundException, RemoteResourceException {
		${entityResourceName.toLowerCase()}ServiceAccess.save(${entityResourceName.toLowerCase()});
	}

	public System${entityResourceName} get(long id) throws ${entityResourceName}NotFoundException {
		return ${entityResourceName.toLowerCase()}ServiceAccess.get(id);
	}

	public void delete(long id) throws ${entityResourceName}NotFoundException, RemoteResourceException {
		System${entityResourceName} u =${entityResourceName.toLowerCase()}ServiceAccess.get(id);
		if(u!=null){
			${entityResourceName.toLowerCase()}ServiceAccess.delete(id);
		}


	}


	public Page<? extends System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
		return ${entityResourceName.toLowerCase()}ServiceAccess.getAll(search,pageNo,pageSize,sortBy,isAscending);
	}

}
