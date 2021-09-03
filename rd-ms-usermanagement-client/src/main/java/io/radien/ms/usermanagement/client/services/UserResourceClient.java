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
package io.radien.ms.usermanagement.client.services;

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

import io.radien.ms.usermanagement.client.entities.GlobalHeaders;
import io.radien.ms.usermanagement.client.entities.User;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

/**
 * User Management REST Requests and command curls
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface UserResourceClient {

    /**
     * Will request the service to retrieve all the users into a paginated response.
     *
     * @param search criteria to be found, can be used to multiple fields
     * @param pageNo page number to show the first records
     * @param pageSize max number of pages of results
     * @param sortBy criteria field to be sorted
     * @param isAscending boolean value to show the values ascending or descending way
     * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
     */
    @GET
    public Response getAll(//@Context HttpSession session, @Context UriInfo uriInfo,
                           @QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    /**
     * Retrieves multiple users into a response in base of a search filter criteria
     * @param sub to be found
     * @param email to be found
     * @param logon to be found
     * @param ids to be found
     * @param isExact should the search fields be exact or not as given
     * @param isLogicalConjunction should the query use a and or a or criteria
     * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
     */
    @GET
    @Path("find")
    public Response getUsers(@QueryParam("sub") String sub,
                             @QueryParam("userEmail") String email,
                             @QueryParam("logon") String logon,
                             @QueryParam("ids") List<Long> ids,
                             @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                             @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    /**
     * Will create request that by a given user sub will try to retrieve the user id
     * @param sub that identifies the user
     * @return Ok message if it has success. Returns error 500 Code to the user in case of resource is not existent.
     */
    @GET
    @Path("/sub/{sub}")
    public Response getUserIdBySub(@PathParam("sub") String sub);

    /**
     * Returns JSON message with the specific required information search by the user ID.
     * @param id to be search
     * @return Ok message if it has success. Returns error 404 Code to the user in case of resource is not existent.
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    /**
     * Deletes requested user from the DB
     * @param id of the user to be deleted
     * @return error 404 Code to the user in case of resource is not existent.
     */
    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    /**
     * Save user to the DB.
     *
     * @param user to be added
     * @return Ok message if it has success. Returns error 400 Code to the user in case of invalid request.
     */
    @POST
    public Response save(User user);

    /**
     * Adds multiple users into the DB.
     *
     * @param userList of users to be added
     * @return returns OK response (Http status 200): All users were added or some users were not added due
     * found issues
     * BAD REQUEST response (Http status 400): None users were added, were found issues for all them
     *
     * For all cases the response must contains the quantity of not added users (not-processed-items),
     * the found issues and an internal status as well (SUCCESS, PARTIAL_SUCCESS and FAIL).
     */
    @POST
    @Path("/multipleCreation")
    public Response create(List<User> userList);

    /**
     * Will send the updated password via email to the user in case of success will return a 200 code message
     * @param id of the user that should the email be sent to
     * @return ok in case the email has been sent with the refreshed password
     */
    @POST
    @Path("/{id}/sendUpdatePasswordEmail")
    public Response sendUpdatePasswordEmail(@NotNull @PathParam("id") long id);

    /**
     * Will update the refresh token, to update the access of the specific user
     * @param refreshToken to be used
     * @return Ok message if it has success. Returns error 500 Code to the user in case of any issue.
     */
    @POST
    @Path("/refresh")
    public Response refreshToken(String refreshToken);

}
