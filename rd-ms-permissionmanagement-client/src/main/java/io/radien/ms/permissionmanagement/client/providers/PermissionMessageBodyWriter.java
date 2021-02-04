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
package io.radien.ms.permissionmanagement.client.providers;

import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.util.PermissionModelMapper;

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
 * @author n.carvalho
 *
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class PermissionMessageBodyWriter implements MessageBodyWriter<Permission> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(Permission.class);
	}

	/*
    Deprecated in JAX RS 2.0
     */
	@Override
	public long getSize(Permission model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	/**
	 * Marshal Permission to OutputStream
	 *
	 * @param model
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @param mediaType
	 * @param httpHeaders
	 * @param entityStream
	 * @throws IOException
	 * @throws WebApplicationException
	 */
	@Override
	public void writeTo(Permission model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		JsonWriter jsonWriter = Json.createWriter(entityStream);
		JsonObject jsonObject = PermissionModelMapper.map(model);
		jsonWriter.writeObject(jsonObject);
		jsonWriter.close();
	}

}
