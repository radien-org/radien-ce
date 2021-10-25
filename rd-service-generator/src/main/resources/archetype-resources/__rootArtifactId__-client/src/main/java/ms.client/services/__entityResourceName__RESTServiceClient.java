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
package ${package}.ms.client.services;

import java.io.InputStream;

import java.net.MalformedURLException;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ${package}.api.entity.Page;
import ${package}.api.OAFAccess;
import ${package}.api.OAFProperties;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}RESTServiceAccess;
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.util.ClientServiceUtil;
import ${package}.ms.client.util.${entityResourceName}ModelMapper;

/**
 * @author Rajesh Gavvala
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
 */
@Stateless
@Default
public class ${entityResourceName}RESTServiceClient implements ${entityResourceName}RESTServiceAccess {
	private static final long serialVersionUID = 6812608123262000047L;

	private static final Logger log = LoggerFactory.getLogger(${entityResourceName}RESTServiceClient.class);

	@Inject
	private OAFAccess oaf;

	@Inject
	private ClientServiceUtil clientServiceUtil;

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	@Override
	public boolean save(System${entityResourceName} ${entityResourceName.toLowerCase()}) {
		try {
			${entityResourceName}ResourceClient client = clientServiceUtil.get${entityResourceName}ResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
			Response response = client.save((${entityResourceName}) ${entityResourceName.toLowerCase()});
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
	public Page<? extends System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException {
		Page<${entityResourceName}> page${entityResourceName}s = null;
		try {
			page${entityResourceName}s = getPage${entityResourceName}s(search, pageNo, pageSize, sortBy, isAscending);
		} catch (Exception e) {

			log.error(e.getMessage());
		}
		return page${entityResourceName}s;
	}

	private Page<${entityResourceName}> getPage${entityResourceName}s(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		Page<${entityResourceName}> page = new Page<>();
		try {
			${entityResourceName}ResourceClient client = clientServiceUtil.get${entityResourceName}ResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
			Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
			page = ${entityResourceName}ModelMapper.mapToPage((InputStream) response.getEntity());

		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return page;
	}

	@Override
	public boolean delete${entityResourceName}(long id) {
		boolean delete${entityResourceName} = false;
		try {
			${entityResourceName}ResourceClient client = clientServiceUtil.get${entityResourceName}ResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT));
			Response response = client.delete(id);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				delete${entityResourceName} = true;
			} else {
				log.error(response.readEntity(String.class));
			}
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return delete${entityResourceName};
	}

}
