/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.ecm.client.providers;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.util.I18NPropertyMapper;

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
 * I18N Property Message writer constructor class validator and converter for returned messages
 *
 * @author Marco Weiland
 */
public class I18NPropertyMessageBodyWriter implements MessageBodyWriter<I18NProperty> {

	/**
	 * Validates if the given message/object is writable or not
	 * @param type of the object
	 * @param genericType generic type of the object
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return true if the given object is a I18N property object
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(I18NProperty.class);
	}

	/**
	 * Calculates the size of the I18N message size
	 * @param model object to be count the size
	 * @param type of the object
	 * @param genericType generic type of the object
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return the object size length
	 */
	@Override
	public long getSize(I18NProperty model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	/**
	 * Will call the I18N Property mapper to convert the given object into a json
	 *
	 * @param model I18N object to be converted
	 * @param type of the object
	 * @param genericType generic type of the received object
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @param httpHeaders header of the http received
	 * @param entityStream received object
	 * @throws WebApplicationException This exception may be thrown by a resource method, provider or StreamingOutput
	 * implementation if a specific HTTP error response needs to be produced.
	 */
	@Override
	public void writeTo(I18NProperty model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws WebApplicationException {
		JsonWriter jsonWriter = Json.createWriter(entityStream);
		JsonObject jsonObject = I18NPropertyMapper.map(model);
		jsonWriter.writeObject(jsonObject);
		jsonWriter.close();
	}
}
