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
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.exception.DocumentTypeException;
import io.radien.api.service.docmanagement.exception.PropertyDefinitionNotFoundException;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionDataAccessLayer;

import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;

import javax.ws.rs.core.Response;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.io.Serializable;

import java.util.List;

@Stateless
public class PropertyDefinitionService implements Serializable {
	@Inject
	private PropertyDefinitionDataAccessLayer propertyDataLayer;

	public Page<? extends SystemPropertyDefinition> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		return propertyDataLayer.getAll(search, pageNo, pageSize, sortBy, isAscending);
	}

	/**
	 * @throws PropertyDefinitionNotFoundException if property for given ID is not available
	 */
	public SystemPropertyDefinition getById(Long id) {
		SystemPropertyDefinition property = propertyDataLayer.get(id);
		if(property == null) {
			throw new PropertyDefinitionNotFoundException("Property type for id " + id + " not available",
					Response.Status.NOT_FOUND);
		}
		return property;
	}

	/**
	 * @throws DocumentTypeException if none of given ids is available
	 */
	public String getNames(List<Long> idList) {
		List<String> nameList = propertyDataLayer.getNames(idList);
		if(nameList.isEmpty()) {
			throw new DocumentTypeException("No properties found for list of IDs provided", Response.Status.BAD_REQUEST);
		}
		return String.join(", ", nameList);
	}

	public void delete(long id) {
		propertyDataLayer.delete(id);
	}

	/**
	 * @throws DocumentTypeException if not unique name is provided
	 */
	public void save(PropertyDefinition property) {
		try {
			propertyDataLayer.save(property);
		} catch (UniquenessConstraintException e) {
			throw new DocumentTypeException(e, Response.Status.BAD_REQUEST);
		}
	}

	public long getTotalRecordsCount() {
		return propertyDataLayer.getTotalRecordsCount();
	}

}
