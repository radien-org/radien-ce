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
package io.rd.microservice.ms.service.services;


import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceServiceAccess;
import io.rd.microservice.exception.MicroserviceNotFoundException;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.exceptions.RemoteResourceException;

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
public class MicroserviceBusinessService implements Serializable {

	private static final long serialVersionUID = 6812608123262000067L;

	private static final Logger log = LoggerFactory.getLogger(MicroserviceBusinessService.class);


	@Inject
	private MicroserviceServiceAccess microserviceServiceAccess;

	public void save(Microservice microservice) throws MicroserviceNotFoundException, RemoteResourceException {
		microserviceServiceAccess.save(microservice);
	}

	public SystemMicroservice get(long id) throws MicroserviceNotFoundException {
		return microserviceServiceAccess.get(id);
	}

	public void delete(long id) throws MicroserviceNotFoundException, RemoteResourceException {
		SystemMicroservice u =microserviceServiceAccess.get(id);
		if(u!=null){
			microserviceServiceAccess.delete(id);
		}


	}


	public Page<? extends SystemMicroservice> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
		return microserviceServiceAccess.getAll(search,pageNo,pageSize,sortBy,isAscending);
	}

}
