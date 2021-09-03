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
package io.radien.ms.rolemanagement.providers;

import io.radien.api.model.role.SystemRole;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.util.RoleModelMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Role JSON reader into object
 * Reads the given JSON object and converts it into a Role
 *
 * @author Bruno Gama
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class SystemRoleMessageBodyReader implements MessageBodyReader<SystemRole> {

    /**
     * Checks if the given JSON object can be read into a Role one
     * @param type of the received object
     * @param genericType for multiple conversion purposes
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @return true in case received JSON can be read into a role
     */
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(RoleEntity.class);
    }

    /**
     * Converts the given JSON object into a role one
     * @param type for the final object (role)
     * @param genericType for multiple conversion purposes
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @param httpHeaders header of the http received
     * @param entityStream received object
     * @return a system role that has been converted from the entity stream
     * @throws WebApplicationException This exception may be thrown by a resource method, provider or StreamingOutput
     * implementation if a specific HTTP error response needs to be produced.
     */
    @Override
    public SystemRole readFrom(Class<SystemRole> type, Type genericType, Annotation[] annotations,
                               MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                               InputStream entityStream) throws WebApplicationException {
        return RoleModelMapper.map(entityStream);
    }
}
