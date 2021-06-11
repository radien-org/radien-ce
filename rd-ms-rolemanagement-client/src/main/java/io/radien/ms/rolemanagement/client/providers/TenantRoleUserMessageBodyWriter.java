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
package io.radien.ms.rolemanagement.client.providers;

import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.util.TenantRoleUserModelMapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Tenant Role User JSON message writer, converts a tenant role into a json object
 *
 * @author Newton Carvalho
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class TenantRoleUserMessageBodyWriter implements MessageBodyWriter<TenantRoleUser> {

    /**
     * Validates if the given received type is a tenant role user object
     * @param type of the received object
     * @param genericType for multiple conversions
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @return true if received object is in fact a tenant role user one
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(TenantRoleUser.class);
    }

    /**
     * Gets the number of received objects and counts it
     * @param model received
     * @param type of the received object
     * @param genericType for multiple conversions
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @return the number of received objects
     */
    @Override
    public long getSize(TenantRoleUser model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    /**
     * Writes the received tenant role user object into a json message
     * @param model received to be written
     * @param type of the received object
     * @param genericType for multiple conversions
     * @param annotations annotation
     * @param mediaType type of the given readable field
     * @param httpHeaders header of the http received
     * @param entityStream received object
     * @throws WebApplicationException in case of error while converting any object field into a json
     * @throws IOException in case of error while receiving or sending the message from or to the user
     */
    @Override
    public void writeTo(TenantRoleUser model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        JsonWriter jsonWriter = Json.createWriter(entityStream);
        JsonObject jsonObject = TenantRoleUserModelMapper.map(model);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();
    }

}
