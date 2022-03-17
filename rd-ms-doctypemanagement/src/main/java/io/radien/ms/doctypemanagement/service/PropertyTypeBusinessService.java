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
package io.radien.ms.doctypemanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.service.docmanagement.exception.DocumentTypeException;
import io.radien.api.service.docmanagement.exception.PropertyTypeNotFoundException;
import io.radien.api.service.docmanagement.propertytype.PropertyTypeDataAccessLayer;

import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.doctypemanagement.client.entities.JCRPropertyType;

import javax.ws.rs.core.Response;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.io.Serializable;

import java.util.List;

@Stateless
public class PropertyTypeBusinessService implements Serializable {
	@Inject
	private PropertyTypeDataAccessLayer propertyTypeService;

	public Page<? extends SystemJCRPropertyType> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		return propertyTypeService.getAll(search, pageNo, pageSize, sortBy, isAscending);
	}

	/**
	 * @throws PropertyTypeNotFoundException if property for given ID is not available
	 */
	public SystemJCRPropertyType getById(Long id) {
		SystemJCRPropertyType property = propertyTypeService.get(id);
		if(property == null) {
			throw new PropertyTypeNotFoundException("Property type for id " + id + " not available",
					Response.Status.NOT_FOUND);
		}
		return property;
	}

	public void delete(long id) {
		propertyTypeService.delete(id);
	}

	/**
	 * @throws DocumentTypeException if not unique name is provided
	 */
	public void save(JCRPropertyType jcrpropertytype) {
		try {
			propertyTypeService.save(jcrpropertytype);
		} catch (UniquenessConstraintException e) {
			throw new DocumentTypeException(e, Response.Status.BAD_REQUEST);
		}
	}

	public long getTotalRecordsCount() {
		return propertyTypeService.getTotalRecordsCount();
	}

}
