/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package ${package}.services;


import ${package}.entities.${resource-name};
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Rajesh Gavvala
 *
 */

@Path("${resource-application-path}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ${resource-name}ResourceClient {
    static final Logger log = LoggerFactory.getLogger(${resource-name}ResourceClient.class);

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id);

    @POST
    public Response create(${resource-name} ${resource-name-variable});

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") long id, ${resource-name} ${resource-name-variable});

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id);
}
