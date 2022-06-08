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
package io.radien.ms.usermanagement.service.jobfair;

import io.radien.ms.openid.entities.Authenticated;
import io.radien.ms.usermanagement.client.entities.JobshopItem;
import io.radien.ms.usermanagement.client.entities.StudentIdentity;
import io.radien.ms.usermanagement.client.services.JobshopResourceClient;
import io.radien.ms.usermanagement.entities.JobshopItemEntity;
import io.radien.ms.usermanagement.entities.StudentIdentityEntity;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Resource class with all the requests to be performed inside the user management
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
@Path("jobshop")
@RequestScoped
@Authenticated
public class JobshopResource implements JobshopResourceClient {

	@Inject
	private StudentIdentityBusinessService identityBusinessService;
	@Inject
	private JobshopItemBusinessService itemBusinessService;

	@Override
	public Response getAllIdentities(String nameFilter, int pageNo, int pageSize) {
		return Response.ok(identityBusinessService.getAll(nameFilter, pageNo, pageSize)).build();
	}

	@Override
	public Response saveIdentity(StudentIdentity studentIdentity) {
		identityBusinessService.save(new StudentIdentityEntity(studentIdentity));
		return Response.ok().build();
	}

	@Override
	public Response getAllItems() {
		return Response.ok(itemBusinessService.getAll()).build();
	}

	@Override
	public Response saveItem(JobshopItem jobshopItem) {
		itemBusinessService.save(new JobshopItemEntity(jobshopItem));
		return Response.ok().build();
	}

	@Override
	public Response saveItems(List<JobshopItem> jobshopItems) {
		jobshopItems.forEach(jobshopItem -> itemBusinessService.save(new JobshopItemEntity(jobshopItem)));
		return Response.ok().build();
	}
}
