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
package io.radien.ms.doctypemanagement.client.providers;

import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.client.util.PropertyDefinitionModelMapper;

import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class PropertyDefinitionMessageBodyWriter implements MessageBodyWriter<PropertyDefinition>{

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type.equals(PropertyDefinition.class);
    }

    @Override
    public long getSize(PropertyDefinition model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(PropertyDefinition model, Class<?> type, Type genericType, Annotation[] annotations,
                        MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
        throws WebApplicationException {

        JsonWriter jsonWriter = Json.createWriter(entityStream);
        JsonObject jsonObject = PropertyDefinitionModelMapper.map(model);
        jsonWriter.writeObject(jsonObject);
        jsonWriter.close();
    }
}
