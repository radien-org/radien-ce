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

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.radien.api.security.TokensPlaceHolder;
import io.radien.ms.usermanagement.client.entities.GlobalHeaders;
import io.radien.ms.usermanagement.client.entities.User;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
@Path("user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface UserResourceClient {

    @GET
    public Response getAll(//@Context HttpSession session, @Context UriInfo uriInfo,
                           @QueryParam("search") String search,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);

    @GET
    @Path("find")
    public Response getUsers(@QueryParam("sub") String sub,
                             @QueryParam("userEmail") String email,
                             @QueryParam("logon") String logon,
                             @DefaultValue("true") @QueryParam("isExact") boolean isExact,
                             @DefaultValue("true") @QueryParam("isLogicalConjunction") boolean isLogicalConjunction);

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    public Response delete(@NotNull @PathParam("id") long id);

    @POST
    public Response save(User user);

    @POST
    @Path("/multipleCreation")
    public Response create(List<User> userList);

    @POST
    @Path("/{id}/sendUpdatePasswordEmail")
    public Response sendUpdatePasswordEmail(@NotNull @PathParam("id") long id);

    @POST
    @Path("/refresh")
    public Response refreshToken(String refreshToken);

}
