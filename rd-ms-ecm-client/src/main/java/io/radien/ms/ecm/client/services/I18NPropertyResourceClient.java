/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

package io.radien.ms.ecm.client.services;

import io.radien.ms.ecm.client.entities.I18NProperty;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("i18n")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface I18NPropertyResourceClient {

    /**
     * Rest request to get a specific I18N Property Resource by a requested message
     * @param msg to be found
     * @return a http response
     */
    @GET
    @Path("")
    Response getMessage(@QueryParam("msg") String msg);

    /**
     * Rest request to get a specific I18N Property Resource by a requested key
     * @param key to be found
     * @return a http response
     */
    @GET
    @Path("/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    Response getProperty(@PathParam("key") String key);

    /**
     * Rest request to get a save a given I18N Property Resource
     * @param property to be stored
     * @return a http response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response add(I18NProperty property);

    /**
     * Rest request to get a save a given I18N Property Resource list of objects
     * @param propertyList list of I18N objects to be stored
     * @return a http response
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response addAll(List<I18NProperty> propertyList);

    /**
     * Rest request to get all the existent keys
     * @return a http response
     */
    @GET
    @Path("/keys")
    Response getKeys();

    /**
     * Rest request to get all the properties
     * @return a http response
     */
    @GET
    @Path("/properties")
    Response getProperties();

    /**
     * Rest request to get all the initialized properties
     * @param secret given secret to match to initialize the properties
     * @return a http response
     */
    @POST
    @Path("/initialize/{secret}")
    Response initializeProperties(@PathParam("secret") String secret);
}
