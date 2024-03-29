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
 *
 */

package io.radien.ms.ticketmanagement.client.services;

import io.radien.ms.openid.entities.GlobalHeaders;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.QueryParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("ticket")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterClientHeaders(GlobalHeaders.class)
public interface TicketResourceClient {

    @GET
    public Response getById(@NotNull @QueryParam("id") Long id);

    @GET
    @Path(("/token"))
    Response getByToken(@NotNull @QueryParam("token") String token);

    @POST
    public Response create(Ticket ticket);

    @PUT
    public Response update(@NotNull @QueryParam("id") long id,Ticket ticket);

    @DELETE
    public Response delete(@NotNull @QueryParam("id") Long id);

    @GET
    @Path("/find")
    public Response getAll(@QueryParam("userId") Long userId,
                           @QueryParam("ticketType") Long ticketType,
                           @QueryParam("expireDate") LocalDate expireDate,
                           @QueryParam("token") String token,
                           @QueryParam("data") String data,
                           @QueryParam("isLogicalConjunction") boolean isLogicalConjunction,
                           @DefaultValue("1")  @QueryParam("pageNo") int pageNo,
                           @DefaultValue("10") @QueryParam("pageSize") int pageSize,
                           @QueryParam("sortBy") List<String> sortBy,
                           @DefaultValue("true") @QueryParam("asc") boolean isAscending);


}
