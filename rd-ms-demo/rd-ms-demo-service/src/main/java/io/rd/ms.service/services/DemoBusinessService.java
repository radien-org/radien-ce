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
package io.rd.ms.service.services;


import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoServiceAccess;
import io.rd.exception.DemoNotFoundException;
import io.rd.ms.client.entities.Demo;
import io.rd.ms.client.exceptions.RemoteResourceException;

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
public class DemoBusinessService implements Serializable {

	private static final long serialVersionUID = 6812608123262000067L;

	private static final Logger log = LoggerFactory.getLogger(DemoBusinessService.class);


	@Inject
	private DemoServiceAccess demoServiceAccess;

	public void save(Demo demo) throws DemoNotFoundException, RemoteResourceException {
		demoServiceAccess.save(demo);
	}

	public SystemDemo get(long id) throws DemoNotFoundException {
		return demoServiceAccess.get(id);
	}

	public void delete(long id) throws DemoNotFoundException, RemoteResourceException {
		SystemDemo u =demoServiceAccess.get(id);
		if(u!=null){
			demoServiceAccess.delete(id);
		}


	}


	public Page<? extends SystemDemo> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
		return demoServiceAccess.getAll(search,pageNo,pageSize,sortBy,isAscending);
	}

}
