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
package io.radien.ms.rolemanagement.providers;

import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.util.RoleModelMapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Role Message body Writer from object to JSON
 *
 * @author Bruno Gama
 */
public class RoleModelMessageBodyWriter implements MessageBodyWriter<Role> {

    /**
     * Validates if the given received type is a role object
     * @param type of the received object
     * @param genericType for multiple conversions
     * @param annotations
     * @param mediaType
     * @return true if received object is in fact a role one
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(Role.class);
    }

    /**
     * Gets the number of received objects and counts it
     * @param role received
     * @param type of the received object
     * @param genericType for multiple conversions
     * @param annotations
     * @param mediaType
     * @return the number of received objects
     */
    @Override
    public long getSize(Role role, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    /**
     * Writes the received role object into a json message
     * @param role received to be written
     * @param type of the received object
     * @param genericType for multiple conversions
     * @param annotations
     * @param mediaType
     * @param httpHeaders
     * @param entityStream
     * @throws WebApplicationException in case of error while converting any object field into a json
     */
    @Override
    public void writeTo(Role role, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws WebApplicationException {
        JsonWriter jsonWriter = Json.createWriter(entityStream);
        JsonObject jsonObject = RoleModelMapper.map(role);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();

    }
}
