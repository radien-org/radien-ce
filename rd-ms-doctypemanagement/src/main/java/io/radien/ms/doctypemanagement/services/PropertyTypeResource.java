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
package io.radien.ms.doctypemanagement.services;

import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;


import io.radien.ms.doctypemanagement.client.entities.JCRPropertyType;
import io.radien.ms.doctypemanagement.client.services.PropertyTypeResourceClient;
import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.doctypemanagement.entities.JCRPropertyTypeEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("propertyType")
@RequestScoped
@Authenticated
public class PropertyTypeResource implements PropertyTypeResourceClient {
	private static final Logger log = LoggerFactory.getLogger(PropertyTypeResource.class);

	@Inject
	private PropertyTypeBusinessService propertyTypeService;

	@Override
	public Response getAll(String search,
						   int pageNo,
						   int pageSize,
						   List<String> sortBy,
						   boolean isAscending) {
		return Response.ok(propertyTypeService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
	}

	@Override
	public Response getById(Long id) {
		SystemJCRPropertyType systemJCRPropertyType = propertyTypeService.getById(id);
		return Response.ok(systemJCRPropertyType).build();
	}

	@Override
	public Response delete(long id) {
		propertyTypeService.delete(id);
		return Response.ok().build();
	}

	@Override
	public Response save(JCRPropertyType propertyType) {
		propertyTypeService.save(new JCRPropertyTypeEntity(propertyType));
		return Response.ok().build();
	}
	@Override
	public Response getTotalRecordsCount() {
		return Response.ok(propertyTypeService.getTotalRecordsCount()).build();
	}
}
