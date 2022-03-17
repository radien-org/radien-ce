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

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.service.docmanagement.propertytype.PropertyTypeServiceAccess;

import io.radien.exception.NotFoundException;
import io.radien.exception.UniquenessConstraintException;

import io.radien.ms.doctypemanagement.client.entities.JCRPropertyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.io.Serializable;

import java.util.List;

@Stateless
public class PropertyTypeBusinessService implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(PropertyTypeBusinessService.class);

	@Inject
	private PropertyTypeServiceAccess propertyTypeService;

	public Page<? extends SystemJCRPropertyType> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		return propertyTypeService.getAll(search, pageNo, pageSize, sortBy, isAscending);
	}

	public SystemJCRPropertyType getById(Long id) throws NotFoundException {
		return propertyTypeService.get(id);
	}

	public void delete(long id) throws NotFoundException {
		propertyTypeService.delete(id);
	}

	public void save(JCRPropertyType jcrpropertytype) throws UniquenessConstraintException, NotFoundException {
		propertyTypeService.save(jcrpropertytype);
	}

	public long getTotalRecordsCount() {
		return propertyTypeService.getTotalRecordsCount();
	}

}
