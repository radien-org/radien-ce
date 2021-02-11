/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.ms.tenantmanagement.client.entities.Contract;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Santana
 */
@Path("contract")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ContractResourceClient {

    @GET
    public Response get(@QueryParam("name") String name);

    @GET
    @Path("/{id}")
    public Response getById(@NotNull @PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    @POST
    public Response create(Contract contract);

    @PUT
    @Path("/{id}")
    public Response update(@NotNull @PathParam("id") long id,Contract contract);

}
