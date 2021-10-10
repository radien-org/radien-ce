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
package io.rd.ms.client.services;

import io.rd.exception.SystemException;
import java.io.InputStream;

import java.net.MalformedURLException;

import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rd.api.entity.Page;
import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoRESTServiceAccess;
import io.rd.ms.client.entities.Demo;
import io.rd.ms.client.util.ClientServiceUtil;
import io.rd.ms.client.util.DemoModelMapper;

/**
 * @author Rajesh Gavvala
 * @author Bruno Gama
 * @author Nuno Santana
 * @author Marco Weiland
 */
@Stateless
@Default
public class DemoRESTServiceClient implements DemoRESTServiceAccess {
	private static final long serialVersionUID = 6812608123262000047L;

	private static final Logger log = LoggerFactory.getLogger(DemoRESTServiceClient.class);

	@Inject
	private OAFAccess oaf;

	@Inject
	private ClientServiceUtil clientServiceUtil;

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	@Override
	public boolean save(SystemDemo demo) {
		try {
			DemoResourceClient client = clientServiceUtil.getDemoResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DEMOMANAGEMENT));
			Response response = client.save((Demo) demo);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				return true;
			} else {
				if(log.isErrorEnabled()){
					log.error(response.readEntity(String.class));
				}
			}
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public Page<? extends SystemDemo> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
		Page<Demo> pageDemos = null;
		try {
			pageDemos = getPageDemos(search, pageNo, pageSize, sortBy, isAscending);
		} catch (Exception e) {

			log.error(e.getMessage());
		}
		return pageDemos;
	}

	private Page<Demo> getPageDemos(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		Page<Demo> page = new Page<>();
		try {
			DemoResourceClient client = clientServiceUtil.getDemoResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DEMOMANAGEMENT));
			Response response = client.getAll(search, pageNo, pageSize, sortBy, isAscending);
			page = DemoModelMapper.mapToPage((InputStream) response.getEntity());

		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return page;
	}

	@Override
	public boolean deleteDemo(long id) {
		boolean deleteDemo = false;
		try {
			DemoResourceClient client = clientServiceUtil.getDemoResourceClient(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DEMOMANAGEMENT));
			Response response = client.delete(id);
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				deleteDemo = true;
			} else {
				if(log.isErrorEnabled()){
					log.error(response.readEntity(String.class));
				}
			}
		} catch (MalformedURLException e) {
			log.error(e.getMessage(), e);
		}
		return deleteDemo;
	}

}
