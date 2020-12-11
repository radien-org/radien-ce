/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.ecm.service;



import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.radien.ms.ecm.model.RadienModel;
import io.radien.ms.ecm.repository.RadienManager;



/**
 * @author Marco Weiland
 *
 */
@Path("models")
@RequestScoped
public class RadienEndpoint {

	@Inject
	private RadienManager manager;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		return Response.ok(manager.getAll()).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getModel(@PathParam("id") String id) {
		RadienModel model = manager.getModel(id);
		return Response.ok(model).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(RadienModel model) {
		String id = manager.add(model);
		return Response.created(UriBuilder.fromResource(this.getClass()).path(id).build()).build();
	}

}
