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
package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

@Path("usermanagement/v1/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserResourceClient {

    @GET
    public Response getAll(@QueryParam("sub") List<String> subs,
                           @QueryParam("userEmail") List<String> emails,
                           @QueryParam("logon") List<String> logons,
                           @QueryParam("pageNo") int pageNo,
                           @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @QueryParam("asc") Boolean isAscending,
                           @QueryParam("isConjunction") Boolean isConjunction);

    @GET
    public Response getById(Long id);

    @PUT
    public Response updateUser(long id, User newUserInformation);

    @DELETE
    public Response deleteOrganization(Long id);

    @POST
    public Response create(User user);
}
