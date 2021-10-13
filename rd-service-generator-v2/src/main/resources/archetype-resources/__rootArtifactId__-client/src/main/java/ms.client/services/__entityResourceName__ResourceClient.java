/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package ${package}.ms.client.services;

import ${package}.ms.client.entities.GlobalHeaders;
import ${package}.ms.client.entities.${entityResourceName};

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
/**
 * ${entityResourceName} RESTResource client
 * @author Bruno Gama
 */
@Path("${entityResourceNameInLowercase}")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface ${entityResourceName}ResourceClient {

    /**
     * Retrieves a page object containing ${entityResourceName.toLowerCase()}s that matches search parameter.
     * In case of omitted (empty) search parameter retrieves ALL ${entityResourceName.toLowerCase()}s
     * @param search search parameter for matching ${entityResourceName.toLowerCase()}s (optional).
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of ${entityResourceName.toLowerCase()}s to be showed in each page.
     * @param sortBy Sorting fields
     * @param isAscending Defines if ascending or descending in relation of sorting fields
     * @return In case of successful operation returns OK (http status 200)
     * and the page object (filled or not).<br>
     * Otherwise, in case of operational error, returns Internal Server Error (500)
     */
    @GET
    public Response getAll(@QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    /**
     * Gets the information of a ${entityResourceName.toLowerCase()} which will be found using the id.
     *
     * @param id to be searched for
     * @return a paginated response with the requested information. 200 code message if success,
     * 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    /**
     * Delete request which will delete the given id ${entityResourceName.toLowerCase()} information
     *
     * @param id record to be deleted
     * @return 200 code message if success, 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Inserts the given ${entityResourceName.toLowerCase()} information, wither creates a new record or updated one already existent one, depending
     * if the given ${entityResourceName.toLowerCase()} has an id or not.
     *
     * @param ${entityResourceNameInLowercase} information to be update or created.
     * @return 200 code message if success, 400 code message if there are duplicated fields that can not be,
     * 404 if ${entityResourceName.toLowerCase()} is not found, 500 code message if there is any error.
     */
    @POST
    public Response save(${entityResourceName} ${entityResourceNameInLowercase});

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent ${entityResourceName.toLowerCase()}s.
     */
    @GET
    @Path("/totalRecords")
    public Response getTotalRecordsCount();

}
