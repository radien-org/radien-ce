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
package ${package}.providers;

import ${package}.entities.${resource-name};
import ${client-packageName}.util.${resource-name}ModelMapper;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;


/**
 * @author Rajesh Gavvala
 *
 */
public class ${resource-name}MessageBodyWriter implements MessageBodyWriter<${resource-name}> {
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(${resource-name}.class);
	}

	/*
	Deprecated in JAX RS 2.0
	*/
	@Override
	public long getSize(${resource-name} model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(${resource-name} model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		JsonWriter jsonWriter = Json.createWriter(entityStream);
		JsonObject jsonObject = ${resource-name}ModelMapper.map(model);
		jsonWriter.writeObject(jsonObject);
		jsonWriter.close();
	}
}
