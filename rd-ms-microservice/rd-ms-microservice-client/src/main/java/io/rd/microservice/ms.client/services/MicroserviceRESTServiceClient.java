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
package io.rd.microservice.ms.client.services;

import java.io.InputStream;

import java.net.MalformedURLException;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.OAFAccess;
import io.rd.microservice.api.OAFProperties;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceRESTServiceAccess;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.util.ClientServiceUtil;
import io.rd.microservice.ms.client.util.MicroserviceModelMapper;

/**
 * @author Rajesh Gavvala
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
 */
@Stateless
@Default
public class MicroserviceRESTServiceClient implements MicroserviceRESTServiceAccess {
	private static final long serialVersionUID = 6812608123262000047L;

	private static final Logger log = LoggerFactory.getLogger(MicroserviceRESTServiceClient.class);

	@Inject
	private OAFAccess oaf;

	@Inject
	private ClientServiceUtil clientServiceUtil;

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	@Override
	public boolean save(SystemMicroservice microservice) {
		try {
			MicroserviceResourceClient client = clientServiceUtil.getMicroserviceResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_MICROSERVICEMANAGEMENT));
			Response response = client.save((Microservice) microservice);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				return true;
			} else {
				log.error(response.readEntity(String.class));
			}
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public Page<? extends SystemMicroservice> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException {
		Page<Microservice> pageMicroservices = null;
		try {
			pageMicroservices = getPageMicroservices(search, pageNo, pageSize, sortBy, isAscending);
		} catch (Exception e) {

			log.error(e.getMessage());
		}
		return pageMicroservices;
	}

	private Page<Microservice> getPageMicroservices(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		Page<Microservice> page = new Page<>();
		try {
			MicroserviceResourceClient client = clientServiceUtil.getMicroserviceResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_MICROSERVICEMANAGEMENT));
			Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
			page = MicroserviceModelMapper.mapToPage((InputStream) response.getEntity());

		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return page;
	}

	@Override
	public boolean deleteMicroservice(long id) {
		boolean deleteMicroservice = false;
		try {
			MicroserviceResourceClient client = clientServiceUtil.getMicroserviceResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_MICROSERVICEMANAGEMENT));
			Response response = client.delete(id);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				deleteMicroservice = true;
			} else {
				log.error(response.readEntity(String.class));
			}
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deleteMicroservice;
	}

}
