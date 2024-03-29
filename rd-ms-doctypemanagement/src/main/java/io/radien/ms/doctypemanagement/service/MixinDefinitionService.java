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
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.service.docmanagement.exception.DocumentTypeException;
import io.radien.api.service.docmanagement.exception.MixinDefinitionNotFoundException;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionDataAccessLayer;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

@Stateless
public class MixinDefinitionService implements Serializable {
	@Inject
	private MixinDefinitionDataAccessLayer mixinDataLayer;

	public Page<? extends SystemMixinDefinition<Long>> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) {
		return mixinDataLayer.getAll(search, pageNo, pageSize, sortBy, isAscending);
	}

	/**
	 * @throws MixinDefinitionNotFoundException if mixin for given ID is not available
	 */
	public SystemMixinDefinition<Long> getById(Long id) {
		SystemMixinDefinition<Long> mixin = mixinDataLayer.get(id);
		if(mixin == null) {
			throw new MixinDefinitionNotFoundException("Mixin type for id " + id + " not available",
					Response.Status.NOT_FOUND);
		}
		return mixin;
	}

	public void delete(long id) {
		mixinDataLayer.delete(id);
	}

	/**
	 * @throws DocumentTypeException if not unique name is provided
	 */
	public void save(MixinDefinitionDTO mixin) {
		try {
			mixinDataLayer.save(mixin);
		} catch (UniquenessConstraintException e) {
			throw new DocumentTypeException(e, Response.Status.BAD_REQUEST);
		}
	}

	public long getTotalRecordsCount() {
		return mixinDataLayer.getTotalRecordsCount();
	}

}
