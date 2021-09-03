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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.entities.GlobalHeaders;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Contract REST service requests
 *
 * @author Santana
 */
@Path("contract")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface ContractResourceClient {

    /**
     * Gets all the contract information into a paginated mode and return those information to the user.
     * @param pageNo page number where the user is seeing the information.
     * @param pageSize number of contract to be showed in each page.
     * @return a paginated response with the information. 200 code message if success, 500 code message if there is any
     * error.
     */
    @GET
    public Response getAll(@DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize);

    /**
     * Method to retrieve a specific contract based on the given name
     * @param name to be search
     * @return the requested contract
     */
    @GET
    public Response get(@QueryParam("name") String name);

    /**
     * Method to get a specific contract based on the requested id
     * @param id to be search
     * @return the requested contract
     */
    @GET
    @Path("/{id}")
    public Response getById(@NotNull @PathParam("id") Long id);

    /**
     * Method to delete a specific contract based on his id
     * @param id of the contract to be deleted
     * @return a response with true or false based on the success or failure of the deletion
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Request to create a specific and given contract object
     * @param contract object information to be created
     * @return a response with true or false based on the success or failure of the creation
     */
    @POST
    public Response create(Contract contract);

    /**
     * Request to update a specific contract
     * @param id of the contract to be update
     * @param contract information to update
     * @return a response with true or false based on the success or failure of the update
     */
    @PUT
    @Path("/{id}")
    public Response update(@NotNull @PathParam("id") long id,Contract contract);

    /**
     * Validates if specific requested Contract exists
     * @param id to be searched
     * @return response true if it exists
     */
    @GET
    @Path("/exists/{id}")
    public Response exists(@NotNull @PathParam("id") Long id);

    /**
     * Will calculate how many records are existent in the db
     * @return the count of existent contracts.
     */
    @GET
    @Path("/countRecords")
    public Response getTotalRecordsCount();
}
