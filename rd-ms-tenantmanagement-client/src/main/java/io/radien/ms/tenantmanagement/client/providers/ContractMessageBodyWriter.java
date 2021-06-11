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
package io.radien.ms.tenantmanagement.client.providers;

import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.util.ContractModelMapper;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Contract Message body Writer from object to JSON
 *
 * @author mawe
 */
public class ContractMessageBodyWriter implements MessageBodyWriter<Contract> {

	/**
	 * Validates if the given received type is a contract object
	 * @param type of the received object
	 * @param genericType for multiple conversions
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @return true if received object is in fact a contract one
	 */
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.equals(Contract.class);
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
	public long getSize(Contract model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	/**
	 * Writes the received contract object into a json message
	 * @param model received to be written
	 * @param type of the received object
	 * @param genericType for multiple conversions
	 * @param annotations annotation
	 * @param mediaType type of the given readable field
	 * @param httpHeaders header of the http received
	 * @param entityStream received object
	 * @throws WebApplicationException This exception may be thrown by a resource method, provider or StreamingOutput
	 * implementation if a specific HTTP error response needs to be produced.
	 */
	@Override
	public void writeTo(Contract model, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		JsonWriter jsonWriter = Json.createWriter(entityStream);
		JsonObject jsonObject = ContractModelMapper.map(model);
		jsonWriter.writeObject(jsonObject);
		jsonWriter.close();
	}
}
