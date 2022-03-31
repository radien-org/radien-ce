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
package io.radien.ms.doctypemanagement.resource;

import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.client.services.MixinDefinitionResourceClient;
import io.radien.ms.doctypemanagement.entities.MixinDefinitionEntity;
import io.radien.ms.doctypemanagement.service.MixinDefinitionService;
import io.radien.ms.openid.entities.Authenticated;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("mixinType")
@RequestScoped
@Authenticated
public class MixinDefinitionResource implements MixinDefinitionResourceClient {
	@Inject
	private MixinDefinitionService mixinTypeService;

	@Override
	public Response getAll(String search, int pageNo, int pageSize,
						   List<String> sortBy, boolean isAscending) {
		return Response.ok(mixinTypeService.getAll(search, pageNo, pageSize, sortBy, isAscending)).build();
	}

	@Override
	public Response getById(Long id) {
		SystemMixinDefinition systemMixinDefinition = mixinTypeService.getById(id);
		return Response.ok(systemMixinDefinition).build();
	}

	@Override
	public Response delete(long id) {
		mixinTypeService.delete(id);
		return Response.ok().build();
	}

	@Override
	public Response save(MixinDefinitionDTO mixinType) {
		mixinTypeService.save(new MixinDefinitionEntity(mixinType));
		return Response.ok().build();
	}

	@Override
	public Response getTotalRecordsCount() {
		return Response.ok(mixinTypeService.getTotalRecordsCount()).build();
	}
}
