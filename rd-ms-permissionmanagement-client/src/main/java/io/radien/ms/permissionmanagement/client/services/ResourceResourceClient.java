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
package io.radien.ms.permissionmanagement.client.services;

import io.radien.ms.permissionmanagement.client.entities.GlobalHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;

@Path("resource")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface ResourceResourceClient {

    /**
     * Retrieves a page object containing resources that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL resources
     * @param search search parameter for matching resources (optional).
     * @param pageNo page number
     * @param pageSize page size
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return In case of successful operation returns OK (http status 200)
     * and the page object (filled or not).
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    public Response getAll(@QueryParam("search") String search,
                    @DefaultValue("1") @QueryParam("pageNo") int pageNo,
                    @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                    @QueryParam("sortBy") List<String> sortBy,
                    @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    /**
     * Finds all resources that matches a name
     * @param name resource name
     * @param isExact indicates if the match is for approximated value or not
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return In case of successful operation returns 200 (http status)
     * and the collection (filled or not).
     * Otherwise, in case of operational error, returns 500
     */
    @GET
    @Path("find")
    public Response getResources(@QueryParam("name") String name,
                        @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                        @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Retrieves an resource by its identifier
     * @param id resource identifier
     * @return If resource exists returns 200 status (and the correspondent object)
     * Otherwise, if does not exist, return 404 status
     * In case of operational error return 500 status
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    /**
     * Deletes an resource by its identifier
     * @param id resource identifier
     * @return Returns 200 status, Otherwise, in case of operational error return 500 status
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Saves an resource (Creation or Update).
     * @param resource resource to be created or update
     * @return Http status 200 in case of successful operation.<br>
     * Bad request (404) in case of trying to create an resource with repeated description.<br>
     * Internal Server Error (500) in case of operational error
     */
    @POST
    public Response save(io.radien.ms.permissionmanagement.client.entities.Resource resource);

}
