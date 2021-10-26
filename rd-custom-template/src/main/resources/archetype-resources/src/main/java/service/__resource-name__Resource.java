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
package ${package}.service;

import io.radien.api.model.${resource-name-variable}.System${resource-name};
import io.radien.api.service.${resource-name-variable}.${resource-name}ServiceAccess;

import ${client-packageName}.entities.${resource-name};
import ${client-packageName}.exceptions.ErrorCodeMessage;
import ${client-packageName}.services.${resource-name}ResourceClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import javax.ws.rs.core.Response;

/**
 *
 * @author Rajesh Gavvala
 */

@RequestScoped
public class ${resource-name}Resource implements ${resource-name}ResourceClient {
    private static final Logger log = LoggerFactory.getLogger(${resource-name}Resource.class);

    @Inject
    private ${resource-name}ServiceAccess ${resource-name-variable}Service;

    @Override
    public Response create(${resource-name} ${resource-name-variable}) {
        try {
            ${resource-name-variable}Service.create(new ${package}.entities.${resource-name}(${resource-name-variable}));
            return Response.ok(${resource-name-variable}.getId()).build();
        }catch (Exception e){
            return getGenericError(e);
        }
    }

    @Override
    public Response get(Long id) {
        try{
        System${resource-name} ${resource-name-variable} = ${resource-name-variable}Service.get(id);
        if(${resource-name-variable} == null){
            return getResourceNotFoundException();
        }
        return Response.ok(${resource-name-variable}).build();
        }catch (Exception e){
            return getGenericError(e);
        }
    }

    @Override
    public Response update(long id, ${resource-name} ${resource-name-variable}) {
        try {
            ${resource-name-variable}.setId(id);
            ${resource-name-variable}Service.update(new ${package}.entities.${resource-name}(${resource-name-variable}));
            return Response.ok().build();
        }catch (Exception e){
            return getGenericError(e);
        }
    }

    @Override
    public Response delete(long id) {
        try {
            ${resource-name-variable}Service.delete(id);
            return Response.ok().build();
        }catch (Exception e){
            return getGenericError(e);
        }
    }

    private Response getGenericError(Exception e) {
        String message = ErrorCodeMessage.GENERIC_ERROR.toString();
        log.error(message, e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(message).build();
    }

    private Response getResourceNotFoundException() {
        return Response.status(Response.Status.NOT_FOUND).entity(ErrorCodeMessage.RESOURCE_NOT_FOUND.toString()).build();
    }

}
